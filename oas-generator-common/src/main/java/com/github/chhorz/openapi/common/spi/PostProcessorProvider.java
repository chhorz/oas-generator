package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.properties.ParserProperties;

public interface PostProcessorProvider {

	OpenAPIPostProcessor create(ParserProperties parserProperties);

}
