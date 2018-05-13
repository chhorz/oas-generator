package com.github.chhorz.openapi.common.properties;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.github.chhorz.openapi.common.domain.Contact;
import com.github.chhorz.openapi.common.domain.ExternalDocumentation;
import com.github.chhorz.openapi.common.domain.Info;
import com.github.chhorz.openapi.common.domain.License;
import com.github.chhorz.openapi.common.domain.Server;

public class DocGeneratorPropertyLoader {

	private Map<String, String> processorOptions;

	private GeneratorProperties properties;

	public DocGeneratorPropertyLoader(final Map<String, String> processorOptions) {
		this.processorOptions = processorOptions;

		loadProperties();
	}

	private void loadProperties() {

		InputStream resourceStream;
		if (processorOptions.get("propertiesPath") == null) {
			System.out.println("Using default properties location.");
			resourceStream = DocGeneratorPropertyLoader.class.getClassLoader()
					.getResourceAsStream("openapigen.yml");
		} else {
			System.out.println("Using custom properties location.");
			resourceStream = DocGeneratorPropertyLoader.class.getClassLoader()
					.getResourceAsStream(processorOptions.get("propertiesPath"));
		}

		try {
			Yaml yaml = new Yaml(new Constructor(GeneratorProperties.class));
			properties = yaml.load(resourceStream);
			System.out.println("Loaded properties");
		} catch (NullPointerException e) {
			// e.printStackTrace();
			properties = new GeneratorProperties();
			System.out.println("Using default properties");
		}
	}

	public Info createInfoFromProperties() {
		Info info = new Info();
		info.setTitle(properties.getInfo().getTitle());
		info.setVersion(properties.getInfo().getVersion());
		info.setContact(createContactFromProperties());
		info.setLicense(createLicenseFromProperties());
		return info;
	}

	private Contact createContactFromProperties() {
		Contact contact = new Contact();
		contact.setName(properties.getContact().getName());
		contact.setEmail(properties.getContact().getEmail());
		contact.setUrl(properties.getContact().getUrl().toString());
		return contact;
	}

	private License createLicenseFromProperties() {
		License license = new License();
		license.setName(properties.getLicense().getName());
		license.setUrl(properties.getLicense().getUrl().toString());
		return license.getName() != null ? license : null;
	}

	public List<Server> createServerFromProperties() {
		return properties.getServers().stream()
				.map(s -> {
					Server server = new Server();
					server.setDescription(s.getDescription());
					server.setUrl(s.getUrl());
					return server;
				})
				.collect(toList());
	}

	public ExternalDocumentation createExternalDocsFromProperties() {
		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setDescription(properties.getExternalDocs().getDescription());
		externalDocs.setUrl(properties.getExternalDocs().getUrl().toString());
		return externalDocs.getUrl() != null ? externalDocs : null;
	}

	public ParserProperties getParserProperties() {
		return properties.getParser();
	}

}
