package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;

public interface OpenAPIPostProcessor {

	void postProcess(ParserProperties parserProperties, OpenAPI openApi);

}
