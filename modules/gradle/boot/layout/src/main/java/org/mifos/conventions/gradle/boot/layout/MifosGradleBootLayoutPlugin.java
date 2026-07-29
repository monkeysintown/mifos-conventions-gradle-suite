///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.boot.layout;

import static java.util.Objects.requireNonNull;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.GRADLE_CONFIGURATION_API;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.GRADLE_CONFIGURATION_IMPLEMENTATION;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_BUILD_GRADLE;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_MODULE_FOLDER;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEPENDENCY_NOTATION;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_MODULE_PREFIX;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_MODULE_SEPARATOR;
import static org.mifos.conventions.gradle.base.core.MifosGradleUtils.visit;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_BACKEND_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_CLI_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_MIGRATION_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_CLI_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_DOC_PLUGIN_ID;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_INTEGRATION_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PERSISTENCE_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_APPLICATION_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CLI_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DATABIND_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DOC_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_IMPLEMENTATION_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_INTEGRATION_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_PERSISTENCE_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SDK_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SERVICE_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SUPPORT_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_TRANSPORT_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_UI_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_USECASE_SUFFIX;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_PROPERTY_BASE_PACKAGE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_SDK_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_SERVICE_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_SUPPORT_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_TRANSPORT_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_UI_PLUGIN_ID_TEMPLATE;
import static org.mifos.conventions.gradle.boot.layout.MifosGradleBootConstants.MIFOS_PROJECT_BOOT_LAYOUT_USECASE_PLUGIN_ID_TEMPLATE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;
import org.mifos.conventions.gradle.base.core.MifosGradleBaseLayoutPlugin;
import org.mifos.conventions.gradle.base.core.extension.MifosGradleExtension;

@Slf4j
public class MifosGradleBootLayoutPlugin implements Plugin<Settings> {
    private final ObjectFactory objects;

    @Inject
    public MifosGradleBootLayoutPlugin(ObjectFactory objects) {
        this.objects = objects;
    }

    @Override
    public void apply(Settings settings) {
        settings.getPlugins().apply(MifosGradleBaseLayoutPlugin.class);

        var mifosExtension = requireNonNull(settings.getExtensions().findByType(MifosGradleExtension.class));
        var project = mifosExtension.getProject();
        var classifier = project.getClassifier().get();
        var modulesPath = settings.getRootDir().toPath().resolve(MIFOS_PROJECT_DEFAULT_MODULE_FOLDER);

        var mainModules = new HashMap<String, Map<String, MifosGradleBootLayoutModule>>();
        var modulesByCategory = new HashMap<String, List<MifosGradleBootLayoutModule>>();
        var mainModulesByPath = new HashMap<String, MifosGradleBootLayoutModule>();
        var modulesByPath = new HashMap<String, MifosGradleBootLayoutModule>();
        var modulesByPathAll = new HashMap<String, MifosGradleBootLayoutModule>();

        visit(
                objects,
                modulesPath,
                details -> {
                    if (details.isDirectory()) return;

                    try {
                        var all = details.getRelativePath().getSegments();
                        var segments = new ArrayList<String>(all.length - 1);
                        for (int i = 0; i < all.length - 1; i++) segments.add(all[i]);
                        var projectId = MIFOS_PROJECT_MODULE_PREFIX
                                + settings.getRootProject().getName()
                                + MIFOS_PROJECT_MODULE_SEPARATOR
                                + segments.stream()
                                        .filter(s -> !s.endsWith(MIFOS_BUILD_GRADLE))
                                        .collect(Collectors.joining(MIFOS_PROJECT_MODULE_SEPARATOR));
                        var projectDir = MIFOS_PROJECT_DEFAULT_MODULE_FOLDER + "/"
                                + requireNonNull(details.getRelativePath().getParent())
                                        .getPathString();

                        var lastIdx = segments.size() - 1;
                        var category = segments.getFirst();
                        var last = segments.getLast();
                        var secondLast = lastIdx >= 1 ? segments.get(lastIdx - 1) : null;
                        var thirdLast = lastIdx >= 2 ? segments.get(lastIdx - 2) : null;
                        var type = lastIdx >= 1
                                ? segments.get(1)
                                : null; // service / sdk / persistence / doc / application...

                        var module = MifosGradleBootLayoutModule.builder()
                                .category(category)
                                .projectId(projectId)
                                .segments(segments)
                                .projectId(projectId)
                                .projectDir(projectDir);

                        // log.error("DOC: {}", module);

                        var m =
                                switch (type) {
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DOC_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_DOC_PLUGIN_ID)
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SDK_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_SDK_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SUPPORT_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_SUPPORT_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SERVICE_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_SERVICE_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_USECASE_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_USECASE_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_INTEGRATION_SUFFIX ->
                                        module.pluginId(
                                                        MIFOS_PROJECT_BOOT_LAYOUT_INTEGRATION_PLUGIN_ID_TEMPLATE
                                                                .formatted(classifier, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_PERSISTENCE_SUFFIX ->
                                        module.pluginId(
                                                        MIFOS_PROJECT_BOOT_LAYOUT_PERSISTENCE_PLUGIN_ID_TEMPLATE
                                                                .formatted(classifier, secondLast, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_TRANSPORT_SUFFIX ->
                                        module.pluginId(
                                                        MIFOS_PROJECT_BOOT_LAYOUT_TRANSPORT_PLUGIN_ID_TEMPLATE
                                                                .formatted(classifier, secondLast, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_UI_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_UI_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, thirdLast, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CLI_SUFFIX ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_CLI_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, thirdLast, last))
                                                .build();
                                    case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_APPLICATION_SUFFIX ->
                                        module.pluginId(
                                                        MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_PLUGIN_ID_TEMPLATE
                                                                .formatted(classifier, secondLast))
                                                .build();
                                    default ->
                                        module.pluginId(MIFOS_PROJECT_BOOT_LAYOUT_PLUGIN_ID_TEMPLATE.formatted(
                                                        classifier, last))
                                                .build();
                                };

                        switch (type) {
                            case MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX,
                                    MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX,
                                    MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DATABIND_SUFFIX,
                                    MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX,
                                    MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DOC_SUFFIX:
                                mainModules
                                        .computeIfAbsent(category, k -> new HashMap<>())
                                        .put(type, m);
                                mainModulesByPath.put(projectId, m);
                                break;
                            default:
                                modulesByCategory
                                        .computeIfAbsent(category, k -> new ArrayList<>())
                                        .add(m);
                                modulesByPath.put(projectId, m);
                        }

                        modulesByPathAll.put(projectId, m);

                        settings.include(m.getProjectId());
                        settings.project(m.getProjectId()).setProjectDir(new File(m.getProjectDir()));
                    } catch (Exception e) {
                        log.error("Wrong: ", e);
                    }
                },
                // "doc/build.gradle",
                "*/core/build.gradle",
                "*/mapping/build.gradle",
                "*/databind/build.gradle",
                "*/starter/build.gradle",
                "*/doc/build.gradle",
                "*/service/*/core/build.gradle",
                "*/service/*/implementation/build.gradle",
                "*/service/*/mapping/build.gradle",
                "*/service/*/starter/build.gradle",
                "*/usecase/*/core/build.gradle",
                "*/usecase/*/implementation/build.gradle",
                "*/usecase/*/mapping/build.gradle",
                "*/usecase/*/starter/build.gradle",
                "*/sdk/*/core/build.gradle",
                "*/sdk/*/implementation/build.gradle",
                "*/sdk/*/starter/build.gradle",
                "*/support/*/core/build.gradle",
                "*/support/*/implementation/build.gradle",
                "*/support/*/mapping/build.gradle",
                "*/support/*/starter/build.gradle",
                "*/persistence/*/core/build.gradle",
                "*/persistence/*/implementation/build.gradle",
                "*/persistence/*/mapping/build.gradle",
                "*/persistence/*/starter/build.gradle",
                "*/transport/*/core/build.gradle",
                "*/transport/*/implementation/build.gradle",
                "*/transport/*/mapping/build.gradle",
                "*/transport/*/starter/build.gradle",
                "*/ui/*/*/core/build.gradle",
                "*/ui/*/*/implementation/build.gradle",
                "*/ui/*/*/mapping/build.gradle",
                "*/ui/*/*/starter/build.gradle",
                "*/cli/*/*/core/build.gradle",
                "*/cli/*/*/implementation/build.gradle",
                "*/cli/*/*/starter/build.gradle",
                "*/integration/*/core/build.gradle",
                "*/integration/*/implementation/build.gradle",
                "*/integration/*/mapping/build.gradle",
                "*/integration/*/starter/build.gradle",
                "*/application/*/implementation/build.gradle");

        settings.getGradle().beforeProject(p -> {
            p.getExtensions()
                    .getExtraProperties()
                    .set(
                            MIFOS_PROJECT_BOOT_LAYOUT_PROPERTY_BASE_PACKAGE,
                            project.getBasePackage().get());

            var path = p.getPath();
            var deps = p.getDependencies();

            // main module
            var main = mainModulesByPath.get(path);

            if (main != null) {
                p.getPlugins().apply(main.getPluginId());

                if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX)) {
                    var cat = main.getCategory();
                    var catMain = mainModules.get(cat);

                    if (catMain != null) {
                        addApiProjectDep(p, catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX));
                        addApiProjectDep(p, catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX));
                        addApiProjectDep(p, catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DATABIND_SUFFIX));
                    }

                    var catMods = modulesByCategory.get(cat);

                    if (catMods != null) {
                        catMods.stream()
                                .parallel()
                                .filter(sm ->
                                        sm.getProjectId().endsWith(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX))
                                .map(sm -> deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, sm.getProjectId())))
                                .forEach(dependency -> deps.add(GRADLE_CONFIGURATION_API, dependency));
                    }
                }
            }

            // non-main module
            var mod = modulesByPath.get(path);
            if (mod == null) {
                return;
            }

            p.getPlugins().apply(mod.getPluginId());

            var cat = mod.getCategory();
            var catMain = mainModules.get(cat);

            if (catMain != null) {
                var mainCore = catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX);

                if (mainCore != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, mainCore.getProjectId())));
                }
            }

            // helper: turn ":a:b:c:implementation" -> ":a:b:c:core"
            if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX)) {
                if (catMain != null) {
                    var mm = catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX);

                    if (mm != null) {
                        deps.add(
                                GRADLE_CONFIGURATION_API,
                                deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, mm.getProjectId())));
                    }
                }

                var core = modulesByPathAll.get(siblingPath(path, MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX));

                if (core != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, core.getProjectId())));
                }
            } else if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_BACKEND_SUFFIX)
                    || path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_CLI_SUFFIX)) {
                if (catMain != null) {
                    var sm = catMain.get(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX);

                    if (sm != null) {
                        deps.add(
                                GRADLE_CONFIGURATION_IMPLEMENTATION,
                                deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, sm.getProjectId())));
                    }
                }
            } else if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_MIGRATION_SUFFIX)) {
                var core = modulesByPathAll.get(siblingPath(path, MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX));

                if (core != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, core.getProjectId())));
                }
            } else if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_IMPLEMENTATION_SUFFIX)) {
                var core = modulesByPathAll.get(siblingPath(path, MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX));

                if (core != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, core.getProjectId())));
                }

                var mapping =
                        modulesByPathAll.get(siblingPath(path, MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX));

                if (mapping != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, mapping.getProjectId())));
                }
            } else if (path.endsWith(MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX)) {
                var implementation = modulesByPathAll.get(
                        siblingPath(path, MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_IMPLEMENTATION_SUFFIX));

                if (implementation != null) {
                    deps.add(
                            GRADLE_CONFIGURATION_API,
                            deps.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, implementation.getProjectId())));
                }
            }
        });
    }

    private static void addApiProjectDep(Project p, MifosGradleBootLayoutModule m) {
        if (m == null) {
            return;
        }
        var d = p.getDependencies();
        d.add(GRADLE_CONFIGURATION_API, d.project(Map.of(MIFOS_PROJECT_DEPENDENCY_NOTATION, m.getProjectId())));
    }

    private static String siblingPath(String path, String newSuffix) {
        int idx = path.lastIndexOf(MIFOS_PROJECT_MODULE_SEPARATOR);
        return idx < 0 ? path : path.substring(0, idx + 1) + newSuffix;
    }
}
