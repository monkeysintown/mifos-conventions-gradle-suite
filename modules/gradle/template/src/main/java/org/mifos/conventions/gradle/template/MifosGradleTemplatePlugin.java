///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.template;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.bundling.Tar;
import org.mifos.conventions.gradle.template.extension.MifosGradleTemplateExtension;
import org.mifos.conventions.gradle.template.task.MifosGradleTemplateIndexCreateTask;

import java.time.LocalDate;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.Optional.ofNullable;
import static org.gradle.api.tasks.bundling.Compression.GZIP;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_EMPTY;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_TASK_GROUP;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TASK_TEMPLATE_BUNDLE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TASK_TEMPLATE_INDEX_CREATE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_BUNDLE_FILE_EXTENSION;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_CLASSIFIER;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_BUG_TRACKER;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_CONTACT;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_COPYRIGHT;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_DESTINATION_FOLDER;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_HOMEPAGE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_INCLUDE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_LICENSE_NAME;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_LICENSE_URL;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PATTERN;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_VENDOR;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_EXTENSION;

@Slf4j
@SuppressWarnings("java:S2094")
public class MifosGradleTemplatePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(PublishingPlugin.class);

        var templateExtension = ofNullable(project.getExtensions().findByType(MifosGradleTemplateExtension.class))
                .orElseGet(() ->
                        project.getExtensions().create(MIFOS_TEMPLATE_EXTENSION, MifosGradleTemplateExtension.class));

        project.getTasks()
                .register(MIFOS_TASK_TEMPLATE_INDEX_CREATE, MifosGradleTemplateIndexCreateTask.class, task -> {
                    task.getFolder().set(templateExtension.getFolder());
                    task.getIncludes()
                            .set(templateExtension.getIncludes().orElse(List.of(MIFOS_TEMPLATE_DEFAULT_INCLUDE)));
                    task.getPattern().set(templateExtension.getPattern().orElse(MIFOS_TEMPLATE_DEFAULT_PATTERN));
                    task.getDesc().set(templateExtension.getDesc().orElse(MIFOS_EMPTY));
                    task.getLicenseName()
                            .set(templateExtension.getLicenseName().orElse(MIFOS_TEMPLATE_DEFAULT_LICENSE_NAME));
                    task.getLicenseUrl()
                            .set(templateExtension.getLicenseUrl().orElse(MIFOS_TEMPLATE_DEFAULT_LICENSE_URL));
                    task.getHomepage().set(templateExtension.getHomepage().orElse(MIFOS_TEMPLATE_DEFAULT_HOMEPAGE));
                    task.getBugTracker()
                            .set(templateExtension.getBugTracker().orElse(MIFOS_TEMPLATE_DEFAULT_BUG_TRACKER));
                    task.getContact().set(templateExtension.getContact().orElse(MIFOS_TEMPLATE_DEFAULT_CONTACT));
                    task.getInceptionYear()
                            .set(templateExtension
                                    .getInceptionYear()
                                    .orElse(LocalDate.now(UTC).getYear() + ""));
                    task.getVendor().set(templateExtension.getVendor().orElse(MIFOS_TEMPLATE_DEFAULT_VENDOR));
                    task.getCopyright().set(templateExtension.getCopyright().orElse(MIFOS_TEMPLATE_DEFAULT_COPYRIGHT));
                    task.getVersion().set(templateExtension.getVersion().orElse("0.1.0-SNAPSHOT"));
                });

        var templatesPath = templateExtension
                .getFolder()
                .orElse(project.getLayout()
                        .getProjectDirectory()
                        .dir("src/main/" + MIFOS_TEMPLATE_DEFAULT_DESTINATION_FOLDER));

        var tarTask = project.getTasks().register(MIFOS_TASK_TEMPLATE_BUNDLE, Tar.class, tar -> {
            tar.setGroup(MIFOS_TASK_GROUP);
            tar.setDescription("Bundle Mifos Templates");
            tar.getArchiveVersion().set(project.getVersion().toString());

            tar.setCompression(GZIP);

            tar.getDestinationDirectory()
                    .set(project.getLayout().getBuildDirectory().dir("distributions"));
            tar.getArchiveBaseName().set(project.getName());
            tar.getArchiveExtension().set(MIFOS_TEMPLATE_BUNDLE_FILE_EXTENSION);

            tar.into(".").from(templatesPath).include(MIFOS_TEMPLATE_DEFAULT_INCLUDE);

            tar.dependsOn(MIFOS_TASK_TEMPLATE_INDEX_CREATE);
            tar.shouldRunAfter(MIFOS_TASK_TEMPLATE_INDEX_CREATE);
        });

        project.getExtensions().configure(PublishingExtension.class, publishing -> {
            project.getPlugins().apply(MavenPublishPlugin.class);

            publishing.publications(
                    publications -> publications.create("template", MavenPublication.class, publication -> {
                        publication.setGroupId(project.getRootProject().getGroup().toString());
                        publication.setVersion(project.getRootProject().getVersion().toString());

                        try {
                            var artifact = publication.artifact(tarTask);
                            artifact.setClassifier(MIFOS_TEMPLATE_CLASSIFIER);
                            artifact.setExtension(MIFOS_TEMPLATE_BUNDLE_FILE_EXTENSION);

                            publication.setArtifacts(List.of(artifact));
                        } catch (UnknownDomainObjectException _) {
                            // ignore
                        }
                    }));
        });
    }
}
