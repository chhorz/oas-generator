package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;

/**
 * This interface declares the actual post processing method.
 *
 * @author chhorz
 */
public interface OpenAPIPostProcessor {

	/**
	 * The actual post processing call.
	 *
	 * @param openApi the parsed {@link OpenAPI} for which the post processing should be done
	 */
	void execute(OpenAPI openApi);

}
