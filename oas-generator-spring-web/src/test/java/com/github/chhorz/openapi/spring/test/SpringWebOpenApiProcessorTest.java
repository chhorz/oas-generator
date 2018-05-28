package com.github.chhorz.openapi.spring.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.controller.ArticleController;
import com.github.chhorz.openapi.spring.test.controller.OrderController;
import com.github.chhorz.openapi.spring.test.controller.resource.Article;
import com.github.chhorz.openapi.spring.test.controller.resource.BaseResource;
import com.github.chhorz.openapi.spring.test.controller.resource.Order;
import com.github.chhorz.openapi.spring.test.controller.resource.PrimitiveResource;

/**
 * http://jcavallotti.blogspot.de/2013/05/how-to-unit-test-annotation-processor.html
 *
 * @author chhorz
 *
 */
public class SpringWebOpenApiProcessorTest extends AbstractProcessorTest {

	@Test
	void aTest() {
		testCompilation(new SpringWebOpenApiProcessor(), OrderController.class, BaseResource.class, Order.class, Article.class,
				PrimitiveResource.class);
	}

	@Test
	@Disabled
	void bTest() {
		testCompilation(new SpringWebOpenApiProcessor(), ArticleController.class);
	}

}
