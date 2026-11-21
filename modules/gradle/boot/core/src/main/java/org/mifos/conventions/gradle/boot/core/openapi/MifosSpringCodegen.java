///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.boot.core.openapi;

import static org.openapitools.codegen.utils.StringUtils.camelize;

import io.swagger.v3.oas.models.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.TemplateManager;
import org.openapitools.codegen.api.TemplatePathLocator;
import org.openapitools.codegen.languages.SpringCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.templating.GeneratorTemplateContentLocator;
import org.openapitools.codegen.templating.TemplateManagerOptions;

/// Spring generator that additionally wraps every operation of a generated `*Api` client in a `MifosUsecase` Spring
/// bean (see `mifos-usecases.mustache`): one `<OperationId>Usecase` class per operation, with nested `Request` and
/// `Response` types, in the package `<mifosUsecaseBasePackage>.<last segment of the OpenAPI tag>`.
///
/// Every operation input becomes an attribute of the use case request: path, query, header, cookie and form
/// parameters one-to-one, an object request body flattened to its top-level properties. Attributes with the same name
/// are merged into one. Non-required attributes are exposed as [Optional].
///
/// Additional properties:
/// - `mifosUsecases`: set to `false` to disable the use case generation
/// - `mifosUsecaseBasePackage`: base package of the use cases (default: the API package)
@Slf4j
public class MifosSpringCodegen extends SpringCodegen {
    public static final String NAME = "mifos-spring";
    public static final String MIFOS_USECASES = "mifosUsecases";
    public static final String MIFOS_USECASE_BASE_PACKAGE = "mifosUsecaseBasePackage";
    public static final String USECASE_TEMPLATE = "mifos-usecases.mustache";

    private static final Pattern INLINE_ENUM = Pattern.compile("(?<![\\w.])([A-Z]\\w*Enum)\\b");
    private static final Pattern TAG_SEPARATOR = Pattern.compile("[./\\\\:>|]+");

    private TemplateManager usecaseTemplates;
    private final Set<File> writtenUsecases = new HashSet<>();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getHelp() {
        return "Generates a Spring client/server with Mifos use case wrappers for every API operation.";
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        var result = super.postProcessOperationsWithModels(objs, allModels);

        if (usecasesEnabled()) {
            var models = allModels.stream()
                    .map(ModelMap::getModel)
                    .collect(Collectors.toMap(m -> m.classname, m -> m, (a, b) -> a));

            for (var operation : result.getOperations().getOperation()) {
                prepareUsecase(operation, models);
                writeUsecase(result, operation);
            }
        }

        return result;
    }

    private void prepareUsecase(CodegenOperation operation, Map<String, CodegenModel> models) {
        var attributes = new LinkedHashMap<String, Attribute>();
        // evaluated after all parameters are processed, since merging clashing inputs can change requiredness
        var args = new ArrayList<Supplier<String>>();
        var bodyAssignments = new ArrayList<Arg>();
        String bodyType = null;

        for (var param : operation.allParams) {
            var bodyModel = param.isBodyParam ? flattenableModel(param, models) : null;

            if (bodyModel != null) {
                bodyType = bodyModel.classname;

                for (var property : topLevelProperties(bodyModel)) {
                    var type = propertyType(bodyModel, property);
                    var attribute = attribute(attributes, property.name, type, property.required);
                    bodyAssignments.add(new Arg(
                            attribute, property.setter, false, converter(operation, attribute, property, type)));
                }
                // "mifosBody" is the local variable the template rebuilds the flattened body into
                var bodyArg = apiExpectsOptional(param) ? "Optional.of(mifosBody)" : "mifosBody";
                args.add(() -> bodyArg);
            } else {
                var attribute = attribute(attributes, param.paramName, paramType(param), param.required);
                args.add(new Arg(attribute, null, apiExpectsOptional(param), null)::expression);
            }
        }

        var argExpressions = args.stream().map(Supplier::get).collect(Collectors.joining(", "));

        var ext = operation.vendorExtensions;
        ext.put("x-mifos-usecase-name", camelize(operation.operationId));
        ext.put(
                "x-mifos-usecase-attributes",
                attributes.values().stream().map(Attribute::toMap).toList());
        ext.put("x-mifos-usecase-args", argExpressions);
        ext.put("x-mifos-usecase-response", response(operation, models));

        if (bodyType != null) {
            ext.put(
                    "x-mifos-usecase-body",
                    Map.of(
                            "type",
                            bodyType,
                            "assignments",
                            bodyAssignments.stream()
                                    .map(a -> Map.of("setter", a.setter(), "value", a.expression()))
                                    .toList()));
        }
    }

    /// Renders one use case file per operation; API templates can only produce one file per tag.
    private void writeUsecase(OperationsMap objs, CodegenOperation operation) {
        var usecasePackage = usecaseBasePackage() + "." + tagPackageSegment(operation);
        var usecaseName = operation.vendorExtensions.get("x-mifos-usecase-name") + "Usecase";

        var data = new HashMap<String, Object>(additionalProperties);
        data.putAll(objs);
        // the *Api class name lives in the nested "operations" map, not at the top level of the API template data
        data.put("classname", objs.getOperations().get("classname"));
        data.put("operation", operation);
        data.put("usecasePackage", usecasePackage);
        data.put("usecaseClassname", usecaseName);

        var file = Path.of(
                        outputFolder(),
                        getSourceFolder(),
                        usecasePackage.replace('.', File.separatorChar),
                        usecaseName + ".java")
                .toFile();

        if (!writtenUsecases.add(file)) {
            log.warn(
                    "Use case {}.{} is generated more than once (operation '{}' in several tags?); overwriting it",
                    usecasePackage,
                    usecaseName,
                    operation.operationId);
        }

        try {
            file.getParentFile().mkdirs();
            usecaseTemplates().write(data, USECASE_TEMPLATE, file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write use case " + file, e);
        }
    }

    private TemplateManager usecaseTemplates() {
        if (usecaseTemplates == null) {
            usecaseTemplates = new TemplateManager(
                    new TemplateManagerOptions(false, false),
                    getTemplatingEngine(),
                    new TemplatePathLocator[] {new GeneratorTemplateContentLocator(this)});
        }

        return usecaseTemplates;
    }

    private String usecaseBasePackage() {
        return Optional.ofNullable(additionalProperties.get(MIFOS_USECASE_BASE_PACKAGE))
                .map(Object::toString)
                .filter(p -> !p.isBlank())
                .orElse(apiPackage());
    }

    /// Package segment for the operation's group: the last segment of the original OpenAPI tag name (e.g.
    /// `clients/loans` → `loans`). Operations are grouped under their sanitized tag (`baseName`), so the original
    /// name is looked up from the operation's tags; without tags the group name itself is used.
    private String tagPackageSegment(CodegenOperation operation) {
        var tag = Optional.ofNullable(operation.tags).orElse(List.of()).stream()
                .map(Tag::getName)
                .filter(name -> sanitizeTag(name).equals(operation.baseName))
                .findFirst()
                .orElse(operation.baseName);

        if (StringUtils.isNotEmpty(tag)) {
            var segments = TAG_SEPARATOR.split(tag.strip());
            var segment = segments.length == 0 ? "" : segments[segments.length - 1];
            segment = segment.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");

            if (segment.isEmpty()) {
                segment = "default";
            }
            if (Character.isDigit(segment.charAt(0))) {
                segment = "_" + segment;
            }
            if (isReservedWord(segment)) {
                segment = segment + "_";
            }

            return segment;
        }

        return "";
    }

    /// Returns the existing attribute with the same name (merging clashing inputs into one field) or creates one.
    private Attribute attribute(Map<String, Attribute> attributes, String name, String type, boolean required) {
        var existing = attributes.get(name);

        if (existing == null) {
            var attribute = new Attribute(name, type, required);
            attributes.put(name, attribute);
            return attribute;
        }

        existing.required |= required;

        return existing;
    }

    /// Converts a merged attribute to the type of a body property when the clashing inputs have different types. Enum
    /// properties (e.g. a `String` path parameter `idType` and a body property `IdTypeEnum idType`) are converted
    /// with
    /// the generated enum's `fromValue`; any other type mismatch is reported and left to the compiler.
    private static String converter(
            CodegenOperation operation, Attribute attribute, CodegenProperty property, String type) {
        if (attribute.type.equals(type)) {
            return null;
        }

        if ((property.isEnum && !property.isContainer) || property.isEnumRef) {
            log.info(
                    "Operation '{}': input '{}' ({}) is converted to body property type {}",
                    operation.operationId,
                    attribute.name,
                    attribute.type,
                    type);
            return type + "::fromValue";
        }

        log.warn(
                "Operation '{}': inputs named '{}' have different types ({} vs {}); keeping a single field of type {}",
                operation.operationId,
                attribute.name,
                attribute.type,
                type,
                attribute.type);
        return null;
    }

    /// Java type of a non-body `*Api` parameter; file uploads are declared as `MultipartFile` by the Spring templates
    /// although the generator models them as `Resource`.
    private static String paramType(CodegenParameter param) {
        if (param.isFile) {
            return param.isArray ? "List<MultipartFile>" : "MultipartFile";
        }

        return param.dataType;
    }

    /// The body model whose top-level properties get flattened, or `null` if the body is kept as a single attribute
    /// (arrays, maps, primitives, free-form objects, models without properties).
    private CodegenModel flattenableModel(CodegenParameter param, Map<String, CodegenModel> models) {
        if (param.isArray || param.isMap || param.isPrimitiveType || param.isFile || param.isBinary) {
            return null;
        }

        var model = Optional.ofNullable(models.get(param.dataType)).orElseGet(() -> models.get(param.baseType));

        if (model == null || model.isEnum || topLevelProperties(model).isEmpty()) {
            return null;
        }

        return model;
    }

    /// Top-level properties of a model, including inherited ones (`allOf`).
    private static List<CodegenProperty> topLevelProperties(CodegenModel model) {
        return model.allVars != null && !model.allVars.isEmpty() ? model.allVars : model.vars;
    }

    /// Inline enums are generated as nested types of the model; qualify them so they resolve in the use case file.
    private static String propertyType(CodegenModel model, CodegenProperty property) {
        if (!property.isEnum && !property.isInnerEnum) {
            return property.datatypeWithEnum;
        }

        return INLINE_ENUM.matcher(property.datatypeWithEnum).replaceAll(model.classname + ".$1");
    }

    /// Mirrors JavaSpring's `optionalDataType` partial: with `useOptional`, every non-required parameter is declared as
    /// `Optional<T>` on the generated `*Api` interface (query, header, cookie, form fields and request bodies)
    /// except
    /// path variables and file uploads (`MultipartFile`).
    private boolean apiExpectsOptional(CodegenParameter param) {
        return booleanProperty("useOptional", false) && !param.required && !param.isPathParam && !param.isFile;
    }

    /// Maps the `*Api` result to use case response attributes: an object model is flattened to its top-level
    /// properties, anything else (simple types, containers, enums, free-form objects) becomes a single `value`
    /// attribute; operations without a response body get an empty response.
    private Map<String, Object> response(CodegenOperation operation, Map<String, CodegenModel> models) {
        var response = new HashMap<String, Object>();
        var type = responseType(operation);

        if (type == null) {
            response.put("void", true);
            response.put("attributes", List.of());
            response.put("assignments", List.of());
            return response;
        }

        var attributes = new ArrayList<Map<String, Object>>();
        var assignments = new ArrayList<Map<String, Object>>();
        var model = operation.returnContainer == null ? models.get(operation.returnType) : null;

        if (model != null && !model.isEnum && !topLevelProperties(model).isEmpty()) {
            for (var property : topLevelProperties(model)) {
                var attribute = new Attribute(property.name, propertyType(model, property), property.required);
                attributes.add(attribute.toMap());
                assignments.add(Map.of(
                        "setter", attribute.toMap().get("setter"), "value", "mifosResult." + property.getter + "()"));
            }
        } else {
            var attribute = new Attribute("value", type, false);
            attributes.add(attribute.toMap());
            assignments.add(Map.of("setter", attribute.toMap().get("setter"), "value", "mifosResult"));
        }

        response.put("void", false);
        response.put("attributes", attributes);
        response.put("assignments", assignments);
        return response;
    }

    /// Java type of the `*Api` result (body), or `null` if the operation has no response body.
    private static String responseType(CodegenOperation operation) {
        if (operation.returnType == null
                || "Void".equals(operation.returnType)
                || "void".equals(operation.returnType)) {
            return null;
        }
        if (operation.returnContainer == null) {
            return operation.returnType;
        }
        if ("Map".equalsIgnoreCase(operation.returnContainer)) {
            return "Map<String, " + operation.returnType + ">";
        }

        return operation.returnContainer + "<" + operation.returnType + ">";
    }

    private boolean usecasesEnabled() {
        return booleanProperty(MIFOS_USECASES, true);
    }

    private boolean booleanProperty(String name, boolean defaultValue) {
        var value = additionalProperties.get(name);
        return value == null ? defaultValue : Boolean.parseBoolean(value.toString());
    }

    private static final class Attribute {
        private final String name;
        private final String type;
        private boolean required;

        private Attribute(String name, String type, boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        private String capitalized() {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }

        private Map<String, Object> toMap() {
            var map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("type", type);
            map.put("getterType", required ? type : "Optional<" + type + ">");
            map.put("getter", "get" + capitalized());
            map.put("setter", "set" + capitalized());
            map.put("optional", !required);
            return map;
        }
    }

    /// Converts a request attribute into the value expected by an `*Api` parameter or a body model setter.
    /// `converter` is an optional method reference (e.g. `Type::fromValue`) applied to the attribute value.
    private record Arg(Attribute attribute, String setter, boolean targetOptional, String converter) {
        private String expression() {
            var getter = "request.get" + attribute.capitalized() + "()";

            if (attribute.required) {
                var value = converter == null ? getter : converter.replace("::", ".") + "(" + getter + ")";
                return targetOptional ? "Optional.ofNullable(" + value + ")" : value;
            }

            var value = converter == null ? getter : getter + ".map(" + converter + ")";
            return targetOptional ? value : value + ".orElse(null)";
        }
    }
}
