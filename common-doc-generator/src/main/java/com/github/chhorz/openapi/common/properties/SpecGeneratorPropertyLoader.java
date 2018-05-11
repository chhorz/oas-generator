package com.github.chhorz.openapi.common.properties;

import static com.github.chhorz.openapi.common.properties.SpecGeneratorProperty.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.github.chhorz.openapi.common.domain.Contact;
import com.github.chhorz.openapi.common.domain.ExternalDocumentation;
import com.github.chhorz.openapi.common.domain.Info;
import com.github.chhorz.openapi.common.domain.License;
import com.github.chhorz.openapi.common.domain.Server;

public class SpecGeneratorPropertyLoader {

	private Map<String, String> processorOptions;

	private Properties properties;

	public SpecGeneratorPropertyLoader(final Map<String, String> processorOptions) {
		this.processorOptions = processorOptions;
		loadProperties();
	}

	private void loadProperties() {
		properties = new Properties();

		InputStream resourceStream;
		if (processorOptions.get("propertiesPath") == null) {
			System.out.println("Using default properties location.");
			resourceStream = SpecGeneratorPropertyLoader.class.getClassLoader().getResourceAsStream("openapi.properties");
		} else {
			System.out.println("Using custom properties location.");
			resourceStream = SpecGeneratorPropertyLoader.class.getClassLoader()
					.getResourceAsStream(processorOptions.get("propertiesPath"));
		}

		try {
			properties.load(resourceStream);
			System.out.println("Loaded properties");
		} catch (IOException | NullPointerException e) {
			// e.printStackTrace();
			System.out.println("Using default properties");
		}
	}

	public Info createInfoFromProperties() {
		Info info = new Info();
		info.setTitle(properties.getProperty(INFO_TITLE, INFO_TITLE_DEFAULT));
		info.setVersion(properties.getProperty(INFO_VERSION, INFO_VERSION_DEFAULT));
		info.setContact(createContactFromProperties());
		info.setLicense(createLicenseFromProperties());
		return info;
	}

	private Contact createContactFromProperties() {
		Contact contact = new Contact();
		contact.setName(properties.getProperty(CONTACT_NAME, CONTACT_NAME_DEFAULT));
		contact.setEmail(properties.getProperty(CONTACT_EMAIL, CONTACT_EMAIL_DEFAULT));
		contact.setUrl(properties.getProperty(CONTACT_URL, CONTACT_URL_DEFAULT));
		return contact;
	}

	private License createLicenseFromProperties() {
		License license = new License();
		license.setName(properties.getProperty(LICENSE_NAME, LICENSE_NAME_DEFAULT));
		license.setUrl(properties.getProperty(LICENSE_URL, LICENSE_URL_DEFAULT));
		return license.getName() != null ? license : null;
	}

	public Server createServerFromProperties() {
		Server server = new Server();
		server.setUrl(properties.getProperty(SERVER_URL, SERVER_URL_DEFAULT));
		server.setDescription(properties.getProperty(SERVER_DESCRIPTION, SERVER_DESCRIPTION_DEFAULT));
		return server.getUrl() != null ? server : null;
	}

	public ExternalDocumentation createExternalDocsFromProperties() {
		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setDescription(properties.getProperty(EXTERNAL_DOCS_DESCRIPTION, EXTERNAL_DOCS_DESCRIPTION_DEFAULT));
		externalDocs.setUrl(properties.getProperty(EXTERNAL_DOCS_URL, EXTERNAL_DOCS_URL_DEFAULT));
		return externalDocs.getUrl() != null ? externalDocs : null;
	}

	public ParserProperties getParserProperties() {
		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setLogLevel(properties.getProperty(PARSER_LOG_LEVEL, PARSER_LOG_LEVEL_DEFAULT));
		parserProperties.setOutputDir(properties.getProperty(PARSER_OUTPUT_DIR, PARSER_OUTPUT_DIR_DEFAULT));
		parserProperties.setOutputFile(properties.getProperty(PARSER_OUTPUT_FILE, PARSER_OUTPUT_FILE_DEFAULT));
		parserProperties.setResourcePackage(properties.getProperty(PARSER_RESOURCE_PACKAGE, PARSER_RESOURCE_PACKAGE_DEFAULT));
		return parserProperties;
	}

}
