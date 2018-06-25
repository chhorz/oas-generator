package com.github.chhorz.openapi.spi;

import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;

public class AsciidoctorProvider implements PostProcessorProvider {

	@Override
	public OpenAPIPostProcessor create() {
		return new AsciidoctorPostProcessor();
	}

}
