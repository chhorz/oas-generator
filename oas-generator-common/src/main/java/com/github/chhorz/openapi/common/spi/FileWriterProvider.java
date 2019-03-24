package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.properties.ParserProperties;

public class FileWriterProvider implements PostProcessorProvider {

	@Override
	public OpenAPIPostProcessor create(final ParserProperties parserProperties) {
		return new FileWriterPostProcessor(parserProperties, Integer.MIN_VALUE);
	}

}
