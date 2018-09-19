package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.properties.ParserProperties;

/**
 * Instances of this interface will be used to create post processor instances.
 *
 * @author chhorz
 */
public interface PostProcessorProvider {

	/**
	 * Creates a post processor instance with the given properties.
	 *
	 * @param parserProperties the properties from the configuration file
	 * @return an instance of the post processor
	 */
	OpenAPIPostProcessor create(ParserProperties parserProperties);

}
