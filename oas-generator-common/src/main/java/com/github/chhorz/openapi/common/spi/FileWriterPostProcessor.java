package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;
import com.github.chhorz.openapi.common.util.LoggingUtils;

public class FileWriterPostProcessor implements OpenAPIPostProcessor {

	private FileUtils fileUtils;
	private LoggingUtils log;

	private int postProcessorOrder;

	public FileWriterPostProcessor(final ParserProperties parserProperties, final int postProcessorOrder) {
		log = new LoggingUtils(parserProperties);
		fileUtils = new FileUtils(parserProperties);

		this.postProcessorOrder = postProcessorOrder;
	}

	@Override
	public void execute(final OpenAPI openApi) {
		log.info("FileWriterPostProcessor | Start");
		fileUtils.writeToFile(openApi);
		log.info("FileWriterPostProcessor | Finish");
	}

	@Override
	public int getPostProcessorOrder() {
		return postProcessorOrder;
	}
}
