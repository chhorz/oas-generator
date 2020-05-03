/**
 *
 *    Copyright 2018-2020 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.chhorz.openapi.common.properties.domain;

import com.github.chhorz.openapi.common.properties.GeneratorPropertiesDefaults;

import java.util.Map;

public class SecuritySchemeOAuthFlowProperties {

	private String authorizationUrl;
	private String tokenUrl;
	private String refreshUrl;
	private Map<String, String> scopes;

	public SecuritySchemeOAuthFlowProperties() {
		this.authorizationUrl = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW_AUTH_URL;
		this.tokenUrl = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW_TOKEN_URL;
		this.refreshUrl = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW_REFRESH_URL;
		this.scopes = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW_SCOPES;
	}

	public String getAuthorizationUrl() {
		return authorizationUrl;
	}

	public void setAuthorizationUrl(String authorizationUrl) {
		this.authorizationUrl = authorizationUrl;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getRefreshUrl() {
		return refreshUrl;
	}

	public void setRefreshUrl(String refreshUrl) {
		this.refreshUrl = refreshUrl;
	}

	public Map<String, String> getScopes() {
		return scopes;
	}

	public void setScopes(Map<String, String> scopes) {
		this.scopes = scopes;
	}
}
