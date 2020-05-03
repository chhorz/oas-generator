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

public class SecuritySchemeOAuthFlowsProperties {

	private SecuritySchemeOAuthFlowProperties implicit;
	private SecuritySchemeOAuthFlowProperties password;
	private SecuritySchemeOAuthFlowProperties clientCredentials;
	private SecuritySchemeOAuthFlowProperties authorizationCode;

	public SecuritySchemeOAuthFlowsProperties() {
		implicit = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW;
		password = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW;
		clientCredentials = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW;
		authorizationCode = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOW;
	}

	public SecuritySchemeOAuthFlowProperties getImplicit() {
		return implicit;
	}

	public void setImplicit(SecuritySchemeOAuthFlowProperties implicit) {
		this.implicit = implicit;
	}

	public SecuritySchemeOAuthFlowProperties getPassword() {
		return password;
	}

	public void setPassword(SecuritySchemeOAuthFlowProperties password) {
		this.password = password;
	}

	public SecuritySchemeOAuthFlowProperties getClientCredentials() {
		return clientCredentials;
	}

	public void setClientCredentials(SecuritySchemeOAuthFlowProperties clientCredentials) {
		this.clientCredentials = clientCredentials;
	}

	public SecuritySchemeOAuthFlowProperties getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(SecuritySchemeOAuthFlowProperties authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
}
