///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.template.task;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.fasterxml.jackson.dataformat.yaml.YAMLParser.Feature.EMPTY_STRING_AS_NULL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.apache.commons.text.CaseUtils.toCamelCase;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_TASK_GROUP;
import static org.mifos.conventions.gradle.base.core.MifosGradleUtils.visit;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_DESTINATION_FOLDER;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_IDX_FILE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_INCLUDE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PARAMETER_DESCRIPTION;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PARAMETER_MESSAGE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PATH_PATTERN;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PATTERN;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_FILE_EXTENSION;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.mifos.conventions.gradle.template.model.MifosGradleTemplateIndex;

@Slf4j
@CacheableTask
public abstract class MifosGradleTemplateIndexCreateTask extends DefaultTask {
    private final ObjectFactory objects;

    @Inject
    public MifosGradleTemplateIndexCreateTask(ObjectFactory objects) {
        this.objects = objects;

        setGroup(MIFOS_TASK_GROUP);
        setDescription("Index Mifos Template Bundles");
    }

    @TaskAction
    public void execute() {
        var templatesPath = getProject()
                .getProjectDir()
                .toPath()
                .resolve(getFolder()
                        .getOrElse(getProject()
                                .getLayout()
                                .getProjectDirectory()
                                .dir("src/main/" + MIFOS_TEMPLATE_DEFAULT_DESTINATION_FOLDER))
                        .toString());

        var idxBuilder = MifosGradleTemplateIndex.builder()
                .metadata(MifosGradleTemplateIndex.TemplateMetadata.builder()
                        .description(getDesc().get())
                        .author("Aleksandar Vidakovic <aleks@mifos.org>")
                        .version(getVersion().get())
                        .build());

        visit(
                objects,
                templatesPath,
                details -> {
                    if (details.isDirectory() && details.getRelativePath().getSegments().length == 1) {
                        var groupBuilder = MifosGradleTemplateIndex.TemplateGroup.builder()
                                .name(details.getPath())
                                .parameters(new ArrayList<>())
                                .files(new ArrayList<>());

                        visit(
                                objects,
                                templatesPath.resolve(details.getPath()),
                                innerDetails -> {
                                    if (innerDetails.isDirectory()) return;

                                    parse(innerDetails, groupBuilder);
                                },
                                getIncludes()
                                        .getOrElse(List.of(MIFOS_TEMPLATE_DEFAULT_INCLUDE))
                                        .toArray(new String[0]));

                        idxBuilder.group(groupBuilder.build());
                    }
                },
                "*");

        var mapper = YAMLMapper.builder()
                .disable(EMPTY_STRING_AS_NULL)
                .disable(FAIL_ON_EMPTY_BEANS)
                .build();

        try {
            var target = templatesPath.resolve(MIFOS_TEMPLATE_DEFAULT_IDX_FILE);
            var result = "";

            if (Files.exists(target)) {
                var idx = merge(mapper.readValue(target.toFile(), MifosGradleTemplateIndex.class), idxBuilder.build());
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(idx);
            } else {
                result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(idxBuilder.build());
            }

            FileUtils.writeStringToFile(target.toFile(), result, UTF_8);

            log.error("INDEX PATH: {}", target);
        } catch (IOException ioe) {
            log.error("Could not write template index: {}", ioe.getMessage());
        }
    }

    private void parse(
            FileVisitDetails details, MifosGradleTemplateIndex.TemplateGroup.TemplateGroupBuilder groupBuilder) {
        var fileBuilder =
                MifosGradleTemplateIndex.TemplateFileDefinition.builder().template(details.getPath());

        String path = details.getPath();

        var paramNames = new HashSet<String>();

        var targetPath = pathParameters(path, groupBuilder, paramNames);

        try (var lines = Files.lines(details.getFile().toPath())) {
            if (path.endsWith(MIFOS_TEMPLATE_FILE_EXTENSION)
                    && lines.map(line -> {
                                boolean hasMatch = false;

                            if (details.getPath().endsWith(MIFOS_TEMPLATE_FILE_EXTENSION)) {
                                    var matcher = Pattern.compile(
                                                    getPattern().getOrElse(MIFOS_TEMPLATE_DEFAULT_PATTERN))
                                            .matcher(line);

                                    while (matcher.find()) {
                                        var result = matcher.group(1);
                                        var param = MifosGradleTemplateIndex.TemplateParameter.builder()
                                                .message(MIFOS_TEMPLATE_DEFAULT_PARAMETER_MESSAGE.formatted(result))
                                                .description(
                                                        MIFOS_TEMPLATE_DEFAULT_PARAMETER_DESCRIPTION.formatted(result))
                                                .name(result)
                                                .type(MifosGradleTemplateIndex.TemplateParameterType.STRING)
                                                .build();

                                        if(!paramNames.contains(param.getName())) {
                                            paramNames.add(param.getName());
                                            groupBuilder.parameter(param);
                                        }

                                        if (!hasMatch) {
                                            hasMatch = true;
                                        }
                                    }
                                }

                                return hasMatch;
                            })
                            .reduce(false, Boolean::logicalOr)) {
                fileBuilder = fileBuilder.type(MifosGradleTemplateIndex.TemplateFileType.PEBBLE);
            } else {
                fileBuilder = fileBuilder.type(MifosGradleTemplateIndex.TemplateFileType.RAW);
            }
        } catch (IOException ioe) {
            log.error("Could not check file for placeholders: {}", path, ioe);
        }

        groupBuilder.file(fileBuilder.template(path).path(targetPath).build());
    }

    private String pathParameters(
            String path, MifosGradleTemplateIndex.TemplateGroup.TemplateGroupBuilder groupBuilder, Set<String> paramNames) {
        var matches = new HashMap<String, String>();
        var matcher = Pattern.compile(getPattern().getOrElse(MIFOS_TEMPLATE_DEFAULT_PATH_PATTERN))
                .matcher(path);

        while (matcher.find()) {
            var result = toCamelCase(matcher.group(0).toLowerCase(Locale.ROOT), false, '_');

            matches.put(matcher.group(0), "{{" + uncapitalize(result) + "}}");

            var param = MifosGradleTemplateIndex.TemplateParameter.builder()
                    .message(MIFOS_TEMPLATE_DEFAULT_PARAMETER_MESSAGE.formatted(result))
                    .description(MIFOS_TEMPLATE_DEFAULT_PARAMETER_DESCRIPTION.formatted(result))
                    .name(result)
                    .type(MifosGradleTemplateIndex.TemplateParameterType.STRING)
                    .build();

            if(!paramNames.contains(param.getName())) {
                paramNames.add(param.getName());
                groupBuilder.parameter(param);
            }

        }

        for (var entry : matches.entrySet()) {
            path = path.replaceAll(entry.getKey(), entry.getValue());
        }

        return path;
    }

    @PathSensitive(PathSensitivity.ABSOLUTE)
    @InputDirectory
    public abstract DirectoryProperty getFolder();

    @Input
    public abstract ListProperty<String> getIncludes();

    @Input
    public abstract Property<String> getPattern();

    @Input
    public abstract Property<String> getDesc();

    @Input
    public abstract Property<String> getLicenseName();

    @Input
    public abstract Property<String> getLicenseUrl();

    @Input
    public abstract Property<String> getHomepage();

    @Input
    public abstract Property<String> getBugTracker();

    @Input
    public abstract Property<String> getContact();

    @Input
    public abstract Property<String> getInceptionYear();

    @Input
    public abstract Property<String> getVendor();

    @Input
    public abstract Property<String> getCopyright();

    @Input
    public abstract Property<String> getVersion();

    private MifosGradleTemplateIndex merge(MifosGradleTemplateIndex oldIdx, MifosGradleTemplateIndex newIdx) {
        var resultIdx = MifosGradleTemplateIndex.builder()
                .metadata(oldIdx.getMetadata())
                .groups(oldIdx.getGroups())
                .build();

        var groupDiff =
                diffByKey(oldIdx.getGroups(), newIdx.getGroups(), MifosGradleTemplateIndex.TemplateGroup::getName);

        if (resultIdx.getGroups() == null) {
            resultIdx.setGroups(new ArrayList<>());
        }

        var g = new ArrayList<>(resultIdx.getGroups());

        if (!groupDiff.added.isEmpty()) {
            g.addAll(groupDiff.added);
            resultIdx.setGroups(g);
        }
        if (!groupDiff.removed.isEmpty()) {
            g.removeAll(groupDiff.removed);
            resultIdx.setGroups(g);
        }

        for (var group : resultIdx.getGroups()) {
            if (group.getParameters() == null) {
                group.setParameters(new ArrayList<>());
            }
            if (group.getFiles() == null) {
                group.setFiles(new ArrayList<>());
            }

            var parameterDiff = diffByKey(
                    group.getParameters(),
                    newIdx.getGroups().stream()
                            .filter(templateGroup -> templateGroup.getName().equals(group.getName()))
                            .findFirst()
                            .map(MifosGradleTemplateIndex.TemplateGroup::getParameters)
                            .orElse(List.of()),
                    MifosGradleTemplateIndex.TemplateParameter::getName);

            var p = new ArrayList<>(group.getParameters());

            if (!parameterDiff.added.isEmpty()) {
                p.addAll(parameterDiff.added);
                group.setParameters(p);
            }

            var filesDiff = diffByKey(
                    group.getFiles(),
                    newIdx.getGroups().stream()
                            .filter(templateGroup -> templateGroup.getName().equals(group.getName()))
                            .findFirst()
                            .map(MifosGradleTemplateIndex.TemplateGroup::getFiles)
                            .orElse(List.of()),
                    MifosGradleTemplateIndex.TemplateFileDefinition::getTemplate);

            var f = new ArrayList<>(group.getFiles());

            if (!filesDiff.added.isEmpty()) {
                f.addAll(filesDiff.added);
                group.setFiles(f);
            }
        }

        return resultIdx;
    }

    private record ListDiff<T>(List<T> added, List<T> removed) {}

    private static <T, K> ListDiff<T> diffByKey(List<T> oldList, List<T> newList, Function<T, K> keyExtractor) {
        Set<K> oldKeys =
                ofNullable(oldList).orElse(List.of()).stream().map(keyExtractor).collect(Collectors.toSet());
        Set<K> newKeys =
                ofNullable(newList).orElse(List.of()).stream().map(keyExtractor).collect(Collectors.toSet());

        List<T> added = newList.stream()
                .filter(i -> !oldKeys.contains(keyExtractor.apply(i)))
                .toList();

        List<T> removed = oldList.stream()
                .filter(i -> !newKeys.contains(keyExtractor.apply(i)))
                .toList();

        return new ListDiff<>(added, removed);
    }
}
