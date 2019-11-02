package com.github.chhorz.openapi.common.properties;

import java.net.URL;
import java.util.*;

import com.github.chhorz.openapi.common.util.LoggingUtils;

public final class GeneratorPropertiesDefaults {

	static final String INFO_TITLE = "Title";
	static final String INFO_DESCRIPTION = null;
	static final URL INFO_TERMS_OF_SERVICE = null;
	static final String INFO_VERSION = "Version";

	static final String CONTACT_NAME = null;
	static final String CONTACT_EMAIL = null;
	static final URL CONTACT_URL = null;

	static final String LICENSE_NAME = null;
	static final URL LICENSE_URL = null;

	public static final String SERVER_URL = null;
	public static final String SERVER_DESCRIPTION = null;

	static final URL EXTERNAL_DOCS_URL = null;
	static final String EXTERNAL_DOCS_DESCRIPTION = null;

	static final String SECURITY_SCHEME_TYPE = null;
	static final String SECURITY_SCHEME_DESCRIPTION = null;
	static final String SECURITY_SCHEME_SCHEME = null;

	static final String TAG_DESCRIPTION = null;

	static final String PARSER_LOG_LEVEL = LoggingUtils.INFO;
	static final String PARSER_OUTPUT_DIR = "./target/openapi";
	static final String PARSER_OUTPUT_FILE = "openapi.json";
	static final String PARSER_SCHEMA_FILE = null;
	static final List<String> PARSER_SCHEMA_PACKAGES = new ArrayList<>();
	static final Map<String, LinkedHashMap> PARSER_POST_PROCESSOR = new LinkedHashMap<>();

	private GeneratorPropertiesDefaults(){
		throw new InstantiationError("Final class must not be instantiated.");
	}

}
