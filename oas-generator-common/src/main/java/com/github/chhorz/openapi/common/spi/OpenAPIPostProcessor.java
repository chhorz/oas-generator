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

	/**
	 * Returns the value for the order in which the post processor should be executed. Possible values are between
	 * {@code Integer.MIN_VALUE} and {@code Integer.MAX_VALUE}. The processors will be executed starting with the
	 * lowest value.
	 *
	 * @return the order in which the post processor should be executed
	 */
	int getPostProcessorOrder();

}
