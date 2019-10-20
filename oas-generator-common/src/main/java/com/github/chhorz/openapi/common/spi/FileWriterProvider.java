package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.properties.ParserProperties;

/**
 * Creates a new {@link PostProcessorProvider} for the {@link FileWriterPostProcessor}.
 *
 * @author chhorz
 */
public class FileWriterProvider implements PostProcessorProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OpenAPIPostProcessor create(final ParserProperties parserProperties) {
		return new FileWriterPostProcessor(parserProperties);
	}

}
