package com.github.chhorz.openapi.common.domain;

import java.util.Map;

/**
 * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#oauth-flow-object
 *
 * @author chhorz
 *
 */
public class OAuthFlow {

	private String authorizationUrl;
	private String tokenUrl;
	private String refreshUrl;
	private Map<String, String> scopes;

}
