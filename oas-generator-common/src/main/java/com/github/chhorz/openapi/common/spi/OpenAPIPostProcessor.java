package com.github.chhorz.openapi.common.spi;

import com.github.chhorz.openapi.common.domain.OpenAPI;

public interface OpenAPIPostProcessor {

	void execute(OpenAPI openApi);

}
