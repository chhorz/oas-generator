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

public class SecuritySchemeProperties {

	private String type;
	private String description;
	private String scheme;
	private String bearerFormat;
	private String name;
	private String in;
	private String openIdConnectUrl;
	private SecuritySchemeOAuthFlowsProperties flows;

	public SecuritySchemeProperties() {
		type = GeneratorPropertiesDefaults.SECURITY_SCHEME_TYPE;
		description = GeneratorPropertiesDefaults.SECURITY_SCHEME_DESCRIPTION;
		scheme = GeneratorPropertiesDefaults.SECURITY_SCHEME_SCHEME;
		bearerFormat = GeneratorPropertiesDefaults.SECURITY_SCHEME_BEARER_FORMAT;
		name = GeneratorPropertiesDefaults.SECURITY_SCHEME_NAME;
		in = GeneratorPropertiesDefaults.SECURITY_SCHEME_IN;
		openIdConnectUrl = GeneratorPropertiesDefaults.SECURITY_SCHEME_OPEN_ID_URL;
		flows = GeneratorPropertiesDefaults.SECURITY_SCHEME_OAUTH_FLOWS;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(final String scheme) {
		this.scheme = scheme;
	}

	public String getBearerFormat() {
		return bearerFormat;
	}

	public void setBearerFormat(String bearerFormat) {
		this.bearerFormat = bearerFormat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}

	public String getOpenIdConnectUrl() {
		return openIdConnectUrl;
	}

	public void setOpenIdConnectUrl(String openIdConnectUrl) {
		this.openIdConnectUrl = openIdConnectUrl;
	}

	public SecuritySchemeOAuthFlowsProperties getFlows() {
		return flows;
	}

	public void setFlows(SecuritySchemeOAuthFlowsProperties flows) {
		this.flows = flows;
	}

}
