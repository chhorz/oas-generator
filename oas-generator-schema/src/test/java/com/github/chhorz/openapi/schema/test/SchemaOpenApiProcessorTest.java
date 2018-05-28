package com.github.chhorz.openapi.schema.test;

import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.schema.SchemaOpenApiProcessor;
import com.github.chhorz.openapi.schema.test.schema.Resource;

public class SchemaOpenApiProcessorTest extends AbstractProcessorTest {

	@Test
	void testAnnotation() {
		testCompilation(new SchemaOpenApiProcessor(), Resource.class);
	}

}
