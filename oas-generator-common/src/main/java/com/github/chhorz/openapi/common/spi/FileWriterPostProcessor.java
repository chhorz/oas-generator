package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.FileUtils;
import com.github.chhorz.openapi.common.util.LoggingUtils;

/**
 * A custom {@link OpenAPIPostProcessor} that writes the OpenAPI domain object to a local file.
 *
 * @author chhorz
 */
public class FileWriterPostProcessor implements OpenAPIPostProcessor {

	private static final Integer POST_PROCESSOR_ORDER = Integer.MIN_VALUE;

	private FileUtils fileUtils;
	private LoggingUtils log;

	FileWriterPostProcessor(final ParserProperties parserProperties) {
		log = new LoggingUtils(parserProperties);
		fileUtils = new FileUtils(parserProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final OpenAPI openApi) {
		log.info("FileWriterPostProcessor | Start");
		fileUtils.writeToFile(openApi);
		log.info("FileWriterPostProcessor | Finish");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPostProcessorOrder() {
		return POST_PROCESSOR_ORDER;
	}
}
