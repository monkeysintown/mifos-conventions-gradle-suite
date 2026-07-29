///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.template.extension;

import static java.time.ZoneOffset.UTC;
import static org.mifos.conventions.gradle.base.core.MifosGradleConstants.MIFOS_EMPTY;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_BUG_TRACKER;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_CONTACT;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_COPYRIGHT;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_HOMEPAGE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_INCLUDE;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_LICENSE_NAME;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_LICENSE_URL;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_PATTERN;
import static org.mifos.conventions.gradle.template.MifosGradleTemplateConstants.MIFOS_TEMPLATE_DEFAULT_VENDOR;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

@Slf4j
public abstract class MifosGradleTemplateExtension {
    public MifosGradleTemplateExtension() {
        getIncludes().set(List.of(MIFOS_TEMPLATE_DEFAULT_INCLUDE));
        getPattern().set(MIFOS_TEMPLATE_DEFAULT_PATTERN);
        getDesc().set(MIFOS_EMPTY);
        getLicenseName().set(MIFOS_TEMPLATE_DEFAULT_LICENSE_NAME);
        getLicenseUrl().set(MIFOS_TEMPLATE_DEFAULT_LICENSE_URL);
        getHomepage().set(MIFOS_TEMPLATE_DEFAULT_HOMEPAGE);
        getBugTracker().set(MIFOS_TEMPLATE_DEFAULT_BUG_TRACKER);
        getContact().set(MIFOS_TEMPLATE_DEFAULT_CONTACT);
        getInceptionYear().set(LocalDate.now(UTC).getYear() + "");
        getVendor().set(MIFOS_TEMPLATE_DEFAULT_VENDOR);
        getCopyright().set(MIFOS_TEMPLATE_DEFAULT_COPYRIGHT);
        getVersion().set("0.1.0-SNAPSHOT");
    }

    public abstract DirectoryProperty getFolder();

    public abstract ListProperty<String> getIncludes();

    public abstract Property<String> getPattern();

    public abstract Property<String> getDesc();

    public abstract Property<String> getLicenseName();

    public abstract Property<String> getLicenseUrl();

    public abstract Property<String> getHomepage();

    public abstract Property<String> getBugTracker();

    public abstract Property<String> getContact();

    public abstract Property<String> getInceptionYear();

    public abstract Property<String> getVendor();

    public abstract Property<String> getCopyright();

    public abstract Property<String> getVersion();
}
