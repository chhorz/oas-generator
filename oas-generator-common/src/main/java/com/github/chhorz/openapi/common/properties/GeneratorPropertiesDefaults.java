package com.github.chhorz.openapi.common.properties;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.github.chhorz.openapi.common.util.LoggingUtils;

public class GeneratorPropertiesDefaults {

	public static final String INFO_TITLE = "Title";
	public static final String INFO_DESCRIPTION = null;
	public static final URL INFO_TERMS_OF_SERVICE = null;
	public static final String INFO_VERSION = "Version";

	public static final String CONTACT_NAME = null;
	public static final String CONTACT_EMAIL = null;
	public static final URL CONTACT_URL = null;

	public static final String LICENSE_NAME = null;
	public static final URL LICENSE_URL = null;

	public static final String SERVER_URL = null;
	public static final String SERVER_DESCRIPTION = null;

	public static final URL EXTERNAL_DOCS_URL = null;
	public static final String EXTERNAL_DOCS_DESCRIPTION = null;

	public static final String SECURITY_SCHEME_TYPE = null;
	public static final String SECURITY_SCHEME_DESCRIPTION = null;
	public static final String SECURITY_SCHEME_SCHEME = null;

	public static final String TAG_DESCRIPTION = null;

	public static final String PARSER_LOG_LEVEL = LoggingUtils.INFO;
	public static final String PARSER_OUTPUT_DIR = "./target/openapi";
	public static final String PARSER_OUTPUT_FILE = "openapi.json";
	public static final List<String> PARSER_SCHEMA_PACKAGES = new ArrayList<>();

}
