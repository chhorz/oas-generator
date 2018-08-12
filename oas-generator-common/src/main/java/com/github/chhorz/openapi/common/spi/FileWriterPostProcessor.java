package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;
import com.github.chhorz.openapi.common.util.LoggingUtils;

public class FileWriterPostProcessor implements OpenAPIPostProcessor {

	private FileUtils fileUtils;
	private LoggingUtils log;

	public FileWriterPostProcessor(final ParserProperties parserProperties) {
		log = new LoggingUtils(parserProperties);
		fileUtils = new FileUtils(parserProperties);
	}

	@Override
	public void execute(final OpenAPI openApi) {
		log.info("FileWriterPostProcessor | START");
		fileUtils.writeToFile(openApi);
		log.info("FileWriterPostProcessor | FINISH");
	}

}
