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
package com.github.chhorz.openapi.common.properties;

import com.github.chhorz.openapi.common.properties.domain.SecuritySchemeOAuthFlowProperties;
import com.github.chhorz.openapi.common.properties.domain.SecuritySchemeOAuthFlowsProperties;
import com.github.chhorz.openapi.common.util.LoggingUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GeneratorPropertiesDefaults {

	public static final String INFO_TITLE = "Title";
	public static final String INFO_DESCRIPTION = null;
	public static final URL INFO_TERMS_OF_SERVICE = null;
	public static final String INFO_VERSION = "Version";

	public static final String CONTACT_NAME = null;
	public static final String CONTACT_EMAIL = null;
	public static final URL CONTACT_URL = null;

	public static final String LICENSE_NAME = null;
	public static final URL LICENSE_URL = null;

	public static final URL EXTERNAL_DOCS_URL = null;
	public static final String EXTERNAL_DOCS_DESCRIPTION = null;

	public static final String SECURITY_SCHEME_TYPE = null;
	public static final String SECURITY_SCHEME_DESCRIPTION = null;
	public static final String SECURITY_SCHEME_SCHEME = null;
	public static final String SECURITY_SCHEME_BEARER_FORMAT = null;
	public static final String SECURITY_SCHEME_NAME = null;
	public static final String SECURITY_SCHEME_IN = null;
	public static final String SECURITY_SCHEME_OPEN_ID_URL = null;
	public static final SecuritySchemeOAuthFlowsProperties SECURITY_SCHEME_OAUTH_FLOWS = null;
	public static final SecuritySchemeOAuthFlowProperties SECURITY_SCHEME_OAUTH_FLOW = null;

	public static final String SECURITY_SCHEME_OAUTH_FLOW_AUTH_URL = null;
	public static final String SECURITY_SCHEME_OAUTH_FLOW_TOKEN_URL = null;
	public static final String SECURITY_SCHEME_OAUTH_FLOW_REFRESH_URL = null;
	public static final Map<String, String> SECURITY_SCHEME_OAUTH_FLOW_SCOPES = null;

	public static final String TAG_DESCRIPTION = null;

	public static final boolean PARSER_ENABLED = Boolean.TRUE;
	public static final String PARSER_LOG_LEVEL = LoggingUtils.INFO;
	public static final String PARSER_OUTPUT_DIR = "./target/openapi";
	public static final String PARSER_OUTPUT_FILE = "openapi";
	public static final String PARSER_OUTPUT_FORMAT = "json,yaml";
	public static final String PARSER_SCHEMA_DIR = "./target/openapi";
	public static final String PARSER_SCHEMA_FILE = "openapi-schema";
	public static final List<String> PARSER_SCHEMA_PACKAGES = new ArrayList<>();
	public static final Map<String, LinkedHashMap> PARSER_POST_PROCESSOR = new LinkedHashMap<>();

	private GeneratorPropertiesDefaults() {
		throw new InstantiationError("Final class must not be instantiated.");
	}

}
