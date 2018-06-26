package com.github.chhorz.openapi.spi;

import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;

public class AsciidoctorProvider implements PostProcessorProvider {

	@Override
	public OpenAPIPostProcessor create(final ParserProperties parserProperties) {
		return new AsciidoctorPostProcessor(parserProperties);
	}

}
