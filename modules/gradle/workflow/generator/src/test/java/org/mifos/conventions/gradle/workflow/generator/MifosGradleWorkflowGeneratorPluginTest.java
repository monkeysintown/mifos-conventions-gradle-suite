///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

@Slf4j
final class MifosGradleWorkflowGeneratorPluginTest {
    @Test
    void parse() throws Exception {
        String code = FileUtils.readFileToString(
                new File("src/test/java/org/mifos/conventions/gradle/workflow/generator/example/CurrencyApi.java"),
                UTF_8);

        // Parse the Java code
        var cu = StaticJavaParser.parse(code);

        // Analyze: Find all method declarations
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            log.error("Method: {}", method.getNameAsString());
            if (method.getType() instanceof ClassOrInterfaceType type) {
                log.error(
                        "Return Type: {}",
                        type.getTypeArguments()
                                .flatMap(NodeList::getFirst)
                                .map(Type::asString)
                                .orElse(null));
            }
        });
    }

    @Test
    void generate() {
        // 1. create the root CompilationUnit
        var cu = new CompilationUnit();

        // 2. add package
        cu.setPackageDeclaration("com.example.generated");

        // 3. add imports
        cu.addImport("java.util.List");
        cu.addImport("java.util.ArrayList");

        // 4. create a public class
        var myClass = cu.addClass("UserService").setPublic(true).addAnnotation("Service");

        // 5. add a private field
        myClass.addField("List<String>", "names").setPrivate(true);

        // 6. add a constructor
        myClass.addConstructor()
                .setPublic(true)
                .setBody(new BlockStmt().addStatement("this.names = new ArrayList<>();"));

        // 7. add a method with logic
        MethodDeclaration method = myClass.addMethod("addName", Modifier.Keyword.PUBLIC)
                .setType("void")
                .addParameter("String", "name");

        method.setBody(new BlockStmt().addStatement("""
                        if (name == null || name.isBlank()) {
                            throw new IllegalArgumentException("Name cannot be empty");
                        } else {
                            this.names.add(name);
                        }
                        """));

        // 8. Add a getter
        myClass.addMethod("getNames", Modifier.Keyword.PUBLIC)
                .setType("List<String>")
                .setBody(new BlockStmt().addStatement("return this.names;"));

        // 9. Print the generated source
        var printer = new DefaultPrettyPrinter(new DefaultPrinterConfiguration());

        var sourceCode = printer.print(cu);

        log.error(sourceCode);
    }
}
