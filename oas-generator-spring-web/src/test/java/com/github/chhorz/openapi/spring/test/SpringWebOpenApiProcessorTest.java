package com.github.chhorz.openapi.spring.test;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.controller.ArticleController;
import com.github.chhorz.openapi.spring.test.controller.HttpMethodsController;
import com.github.chhorz.openapi.spring.test.controller.OrderController;
import com.github.chhorz.openapi.spring.test.controller.external.ExternalResource;
import com.github.chhorz.openapi.spring.test.controller.resource.*;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;

/**
 * http://jcavallotti.blogspot.de/2013/05/how-to-unit-test-annotation-processor.html
 *
 * @author chhorz
 */
class SpringWebOpenApiProcessorTest extends AbstractProcessorTest {

    @Test
    void aTest() {
        // run annotation processor
        testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator01.yml"), OrderController.class, BaseResource.class, Order.class, Article.class,
                PrimitiveResource.class, ErrorResource.class, ExternalResource.class);

        // compare result with reference documentation
        compareFileContent("expected/openapi01.json", "oas-test/openapi01.json");
    }

    @Test
    void testArticleController() {
        // run annotation processor
        testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator02.yml"),
                ArticleController.class, Article.class);

        // compare result with reference documentation
        compareFileContent("expected/openapi02.json", "oas-test/openapi02.json");
    }

    @Test
    void testHttpMethods() {
        // run annotation processor
        testCompilation(new SpringWebOpenApiProcessor(), createConfigMap("oas-generator03.yml"),
                HttpMethodsController.class, Article.class);

        // compare result with reference documentation
        compareFileContent("expected/openapi03.json", "oas-test/openapi03.json");
    }

    /**
     * Compares JSON files for identical content. The order of properties is not checked. If the content does not match the test will be failed.
     *
     * @param expectedFile Filename of the expected json content. The file must be placed in {@code src/test/resources/} directory.
     * @param actualFile   Filename of the actual json content. The file must be placed in {@code target/} directory.
     */
    private void compareFileContent(String expectedFile, String actualFile) {
        try {
            String expected = String.join("", Files.readAllLines(Paths.get("src/test/resources/" + expectedFile)));
            String actual = String.join("", Files.readAllLines(Paths.get("target/" + actualFile)));
            JSONAssert.assertEquals(expected, actual, true);
        } catch (JSONException | IOException e) {
            fail("Could not check openapi.json against expected file.", e);
        }
    }

    private Map<String, String> createConfigMap(String configFile) {
        return Collections.singletonMap(OpenAPIConstants.OPTION_PROPERTIES_PATH, configFile);
    }

}
