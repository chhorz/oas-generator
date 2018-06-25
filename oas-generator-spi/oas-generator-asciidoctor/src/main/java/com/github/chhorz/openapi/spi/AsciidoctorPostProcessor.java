package com.github.chhorz.openapi.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.util.LoggingUtils;

public class AsciidoctorPostProcessor implements OpenAPIPostProcessor {

	@Override
	public void postProcess(final ParserProperties parserProperties, final OpenAPI openApi) {
		LoggingUtils log = new LoggingUtils(parserProperties);
		log.info("AsciidoctorPostProcessor | START");
	}

}
