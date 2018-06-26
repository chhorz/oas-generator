package com.github.chhorz.openapi.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.util.LoggingUtils;

public class AsciidoctorPostProcessor implements OpenAPIPostProcessor {

	private LoggingUtils log;

	public AsciidoctorPostProcessor(final ParserProperties parserProperties) {
		log = new LoggingUtils(parserProperties);
	}

	@Override
	public void execute(final OpenAPI openApi) {
		log.info("AsciidoctorPostProcessor | START");
	}

}
