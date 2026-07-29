///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.boot.layout;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
class MifosGradleBootLayoutModule {
    private String category;
    private String projectId;
    private List<String> segments;
    private String pluginId;
    private String projectDir;
}
