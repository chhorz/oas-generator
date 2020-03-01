package com.github.chhorz.openapi.common.domain;

import java.util.Map;

/**
 * https://spec.openapis.org/oas/v3.0.3#oauth-flow-object
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
