///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.boot.layout;

import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECTS_PROPERTY_PREFIX;

public final class MifosGradleBootConstants {
    private MifosGradleBootConstants() {}

    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROPERTY_BASE_PACKAGE =
            MIFOS_PROJECTS_PROPERTY_PREFIX + "base.package";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CORE_SUFFIX = "core";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_MAPPING_SUFFIX = "mapping";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_IMPLEMENTATION_SUFFIX = "implementation";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_STARTER_SUFFIX = "starter";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DATABIND_SUFFIX = "databind";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SDK_SUFFIX = "sdk";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SUPPORT_SUFFIX = "support";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_SERVICE_SUFFIX = "service";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_USECASE_SUFFIX = "usecase";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_INTEGRATION_SUFFIX = "integration";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_PERSISTENCE_SUFFIX = "persistence";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_TRANSPORT_SUFFIX = "transport";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_UI_SUFFIX = "ui";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_CLI_SUFFIX = "cli";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_APPLICATION_SUFFIX = "application";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PROJECT_ID_DOC_SUFFIX = "doc";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PLUGIN_ID_TEMPLATE = "org.mifos.conventions.gradle.boot.%s.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_PERSISTENCE_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.persistence.%s.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_TRANSPORT_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.transport.%s.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_UI_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.ui.%s.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_CLI_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.cli.%s.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_SDK_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.sdk.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_SERVICE_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.service.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_USECASE_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.usecase.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_INTEGRATION_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.integration.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_SUPPORT_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.support.%s";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_PLUGIN_ID_TEMPLATE =
            "org.mifos.conventions.gradle.boot.%s.application.%s.implementation";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_DOC_PLUGIN_ID = "org.mifos.conventions.gradle.base.doc";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_CLI_SUFFIX = "-application-cli-implementation";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_BACKEND_SUFFIX =
            "-application-backend-implementation";
    public static final String MIFOS_PROJECT_BOOT_LAYOUT_APPLICATION_MIGRATION_SUFFIX =
            "-application-migration-implementation";
}
