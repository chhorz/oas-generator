package com.github.chhorz.openapi.common.spi;

public class FileWriterProvider implements PostProcessorProvider {

	@Override
	public OpenAPIPostProcessor create() {
		return new FileWriterPostProcessor();
	}

}
