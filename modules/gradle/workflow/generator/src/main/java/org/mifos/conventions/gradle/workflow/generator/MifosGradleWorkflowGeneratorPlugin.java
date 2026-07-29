///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

@Slf4j
@SuppressWarnings("java:S2094")
public class MifosGradleWorkflowGeneratorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        log.error("TODO: implement this!");

        createClass("Dummy", "org.mifos.dummy", List.of("java.util.List"));
    }

    private ClassOrInterfaceDeclaration createClass(String name, String pkg, List<String> importStatements) {
        var cu = new CompilationUnit();

        cu.setPackageDeclaration(pkg);

        for (var importStatement : importStatements) {
            cu.addImport(importStatement);
        }

        return cu.addClass(name).setPublic(true).addAnnotation("Component");
    }
}
