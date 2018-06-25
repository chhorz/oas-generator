package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;
import com.github.chhorz.openapi.common.util.LoggingUtils;

public class FileWriterPostProcessor implements OpenAPIPostProcessor {

	@Override
	public void postProcess(final ParserProperties parserProperties, final OpenAPI openApi) {
		LoggingUtils log = new LoggingUtils(parserProperties);
		log.info("FileWriterPostProcessor | START");
		FileUtils fileUtils = new FileUtils(parserProperties);
		fileUtils.writeToFile(openApi);
	}

}
