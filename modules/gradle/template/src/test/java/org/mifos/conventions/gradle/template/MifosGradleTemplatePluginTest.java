///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.template;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TASK_TEMPLATE_BUNDLE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TASK_TEMPLATE_INDEX_CREATE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_IDX_FILE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_PLUGIN_ID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mifos.conventions.gradle.template.extension.MifosGradleTemplateExtension;

@Slf4j
class MifosGradleTemplatePluginTest {
    @TempDir
    private File projectDir;

    @BeforeEach
    void setUp() throws Exception {
        var build = """
plugins {
    id "org.mifos.conventions.gradle.template"
}

version = "0.0.1-SNAPSHOT"

mifosTemplate {
    folder = "/home/spaddo/workspace/mifos/mifos-conventions-gradle-suite/modules/gradle/template/src/test/templates"
    includes = ["**/*"]
    // desc = "Mifos Test"
    inceptionYear = "2026"
    version = "${project.version?:'0.1.0-SNAPSHOT'}"
}
""";

        var settings = """
buildscript {
    repositories {
        mavenCentral().content {
            excludeModule("javax.media", "jai_core")
        }
        gradlePluginPortal()
        maven {
            url = uri("https://mifos.jfrog.io/artifactory/mifosx-gradle-local")
        }
    }

    dependencies {
        classpath "org.mifos.conventions.gradle:mifos-conventions-gradle-template:0.1.0-SNAPSHOT"
    }
}

// apply plugin: "org.mifos.conventions.gradle.base.layout"

rootProject.name = "mifos-test"

//mifos {
//    project {
//        groupId = "org.mifos.test"
//        description = "Mifos Test"
//        basePackage = "org.mifos.test"
//        inceptionYear = "2026"
//    }
//    developers {
//        "vidakovic" {
//            firstname = "Aleksandar"
//            lastname = "Vidakovic"
//            email = "aleks@mifos.org"
//            timezone = "UTC+2"
//            roles = ["developer", "architect"]
//        }
//    }
//}
""";
        FileUtils.writeStringToFile(projectDir.toPath().resolve("build.gradle").toFile(), build, UTF_8);
        FileUtils.writeStringToFile(
                projectDir.toPath().resolve("settings.gradle").toFile(), settings, UTF_8);
    }

    @AfterEach
    void tearDown() throws IOException {
        // NOTE: clean up
        var basePath = "src/test/templates";

        var idxFile = Path.of(basePath).resolve(MIFOS_TEMPLATE_DEFAULT_IDX_FILE);

        if (Files.exists(idxFile)) {
            log.error("Cleanup: {}", idxFile.toFile().getAbsolutePath());
            Files.delete(idxFile);
        }
    }

    @Test
    void pluginRegistration() {
        // create an in-memory project
        var project = ProjectBuilder.builder().withProjectDir(projectDir).build();

        // apply the plugin
        project.getPlugins().apply(MIFOS_TEMPLATE_PLUGIN_ID);

        // verify extension registration
        var templateExtension = project.getExtensions().findByType(MifosGradleTemplateExtension.class);
        assertNotNull(templateExtension);
        assertEquals("", templateExtension.getDesc().get());
        assertEquals("2026", templateExtension.getInceptionYear().get());
        assertEquals("0.1.0-SNAPSHOT", templateExtension.getVersion().get());

        // verify task registration
        var indexCreateTask = project.getTasks().findByName(MIFOS_TASK_TEMPLATE_INDEX_CREATE);
        assertNotNull(indexCreateTask);

        var bundleTask = project.getTasks().findByName(MIFOS_TASK_TEMPLATE_BUNDLE);
        assertNotNull(bundleTask);
    }

    @Test
    @Disabled("Takes too long to download Gradle environment")
    void executeTasks() throws IOException {
        var result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withDebug(true)
                .withArguments(MIFOS_TASK_TEMPLATE_BUNDLE, "publish", "--info")
                .forwardOutput()
                .build();

        assertEquals(3, result.getTasks().size());
        assertEquals(
                SUCCESS,
                requireNonNull(result.task(":" + MIFOS_TASK_TEMPLATE_INDEX_CREATE))
                        .getOutcome());
        assertEquals(
                SUCCESS,
                requireNonNull(result.task(":" + MIFOS_TASK_TEMPLATE_BUNDLE)).getOutcome());

        Files.list(projectDir.toPath().resolve("build", "distributions")).forEach(path -> {
            log.error("FOUND: {}", path.toAbsolutePath());
        });
    }
}
