///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator;

import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_EXTENSION;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PLUGIN_ID_PREFIX;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_BUG_TRACKER;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_CONTACT;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_COPYRIGHT;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_HOMEPAGE;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_LICENSE_NAME;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_LICENSE_URL;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_PROJECT_DEFAULT_VENDOR;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_TASK_PREFIX;

public final class MifosGradleWorkflowGeneratorConstants {
    private MifosGradleWorkflowGeneratorConstants() {}

    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_PATTERN = "\\{\\{\\s*([\\w\\d]+?)\\s*\\}\\}";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_PATH_PATTERN = "__([\\w\\d]+[_\\w\\d]+?)__";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_INCLUDE = "**/*";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_DESTINATION_FOLDER = "templates";
    public static final String MIFOS_WORKFLOW_GENERATOR_BUNDLE_FILE_EXTENSION = "tgz";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_IDX_FILE = ".mifos.tpl.idx.yml";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_PARAMETER_MESSAGE = "Enter value for parameter '%s'";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_PARAMETER_DESCRIPTION =
            "[TODO: please provide description for parameter '%s']";
    public static final String MIFOS_WORKFLOW_GENERATOR_FILE_EXTENSION = ".peb";
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_LICENSE_NAME = MIFOS_PROJECT_DEFAULT_LICENSE_NAME;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_LICENSE_URL = MIFOS_PROJECT_DEFAULT_LICENSE_URL;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_HOMEPAGE = MIFOS_PROJECT_DEFAULT_HOMEPAGE;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_BUG_TRACKER = MIFOS_PROJECT_DEFAULT_BUG_TRACKER;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_CONTACT = MIFOS_PROJECT_DEFAULT_CONTACT;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_VENDOR = MIFOS_PROJECT_DEFAULT_VENDOR;
    public static final String MIFOS_WORKFLOW_GENERATOR_DEFAULT_COPYRIGHT = MIFOS_PROJECT_DEFAULT_COPYRIGHT;
    public static final String MIFOS_WORKFLOW_GENERATOR_PLUGIN_ID = MIFOS_PLUGIN_ID_PREFIX + ".template";
    public static final String MIFOS_WORKFLOW_GENERATOR_EXTENSION = MIFOS_EXTENSION + "Template";
    public static final String MIFOS_TASK_WORKFLOW_GENERATOR_PREFIX = MIFOS_TASK_PREFIX + "Template";
    public static final String MIFOS_TASK_WORKFLOW_GENERATOR_INDEX_CREATE =
            MIFOS_TASK_WORKFLOW_GENERATOR_PREFIX + "IndexCreate";
    public static final String MIFOS_TASK_WORKFLOW_GENERATOR_BUNDLE = MIFOS_TASK_WORKFLOW_GENERATOR_PREFIX + "Bundle";
}
