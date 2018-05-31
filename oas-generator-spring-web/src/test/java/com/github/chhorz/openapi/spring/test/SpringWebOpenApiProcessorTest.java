package com.github.chhorz.openapi.spring.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;
import com.github.chhorz.openapi.spring.test.controller.ArticleController;
import com.github.chhorz.openapi.spring.test.controller.OrderController;
import com.github.chhorz.openapi.spring.test.controller.external.ExternalResource;
import com.github.chhorz.openapi.spring.test.controller.resource.Article;
import com.github.chhorz.openapi.spring.test.controller.resource.BaseResource;
import com.github.chhorz.openapi.spring.test.controller.resource.ErrorResource;
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
				PrimitiveResource.class, ErrorResource.class, ExternalResource.class);
	}

	@Test
	@Disabled
	void bTest() {
		testCompilation(new SpringWebOpenApiProcessor(), ArticleController.class);
	}

}
