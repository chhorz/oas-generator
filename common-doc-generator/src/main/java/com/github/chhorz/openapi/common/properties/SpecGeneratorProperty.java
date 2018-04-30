package com.github.chhorz.openapi.common.properties;

public class SpecGeneratorProperty {

	// property keys
	public static final String INFO_TITLE = "info.title";
	public static final String INFO_VERSION = "info.version";

	public static final String CONTACT_NAME = "contact.name";
	public static final String CONTACT_EMAIL = "contact.email";
	public static final String CONTACT_URL = "contact.url";

	public static final String LICENSE_NAME = "license.name";
	public static final String LICENSE_URL = "license.url";

	public static final String SERVER_URL = "server.url";
	public static final String SERVER_DESCRIPTION = "server.description";

	public static final String EXTERNAL_DOCS_URL = "external.docs.url";
	public static final String EXTERNAL_DOCS_DESCRIPTION = "external.docs.description";

	public static final String PARSER_OUTPUT_DIR = "parser.output.dir";
	public static final String PARSER_OUTPUT_FILE = "parser.output.file";
	public static final String PARSER_RESOURCE_PACKAGE = "parser.resource.package";

	// property default values
	public static final String INFO_TITLE_DEFAULT = "Title";
	public static final String INFO_VERSION_DEFAULT = "Version";

	public static final String CONTACT_NAME_DEFAULT = "Author";
	public static final String CONTACT_EMAIL_DEFAULT = "E-Mail";
	public static final String CONTACT_URL_DEFAULT = null;

	public static final String LICENSE_NAME_DEFAULT = null;
	public static final String LICENSE_URL_DEFAULT = null;

	public static final String SERVER_URL_DEFAULT = null;
	public static final String SERVER_DESCRIPTION_DEFAULT = null;

	public static final String EXTERNAL_DOCS_URL_DEFAULT = null;
	public static final String EXTERNAL_DOCS_DESCRIPTION_DEFAULT = null;

	public static final String PARSER_OUTPUT_DIR_DEFAULT = "./target/openapi";
	public static final String PARSER_OUTPUT_FILE_DEFAULT = "openapi.json";
	public static final String PARSER_RESOURCE_PACKAGE_DEFAULT = null;
}
