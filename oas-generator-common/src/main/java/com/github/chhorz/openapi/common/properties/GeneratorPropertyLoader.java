package com.github.chhorz.openapi.common.properties;

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.domain.Contact;
import com.github.chhorz.openapi.common.domain.ExternalDocumentation;
import com.github.chhorz.openapi.common.domain.Info;
import com.github.chhorz.openapi.common.domain.License;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.domain.SecurityScheme.Type;
import com.github.chhorz.openapi.common.domain.SecuritySchemeHttp;
import com.github.chhorz.openapi.common.domain.Server;

public class GeneratorPropertyLoader {

	private Map<String, String> processorOptions;

	private GeneratorProperties properties;

	public GeneratorPropertyLoader(final Map<String, String> processorOptions) {
		this.processorOptions = processorOptions;

		loadProperties();
	}

	private void loadProperties() {

		InputStream resourceStream;
		if (processorOptions.get("propertiesPath") == null) {
			System.out.println("Using default properties location.");
			resourceStream = GeneratorPropertyLoader.class.getClassLoader().getResourceAsStream("oas-generator.yml");
		} else {
			System.out.println("Using custom properties location.");
			resourceStream = GeneratorPropertyLoader.class.getClassLoader()
					.getResourceAsStream(processorOptions.get("propertiesPath"));
		}

		try {
			Yaml yaml = new Yaml(new Constructor(GeneratorProperties.class));
			properties = yaml.load(resourceStream);
			System.out.println("Loaded properties");
		} catch (Exception e) {
			// e.printStackTrace();
			properties = new GeneratorProperties();
			System.out.println("Using default properties");
		}
	}

	public Info createInfoFromProperties() {
		InfoProperties infoProperties = properties.getInfo();

		String version = processorOptions.getOrDefault(OpenAPIConstants.OPTION_VERSION, null);

		Info info = new Info();
		info.setTitle(infoProperties.getTitle());
		info.setDescription(infoProperties.getDescription());
		info.setTermsOfService(resolveUrl(infoProperties.getTermsOfService()));
		info.setContact(createContactFromProperties(infoProperties));
		info.setLicense(createLicenseFromProperties(infoProperties));
		info.setVersion(version == null ? infoProperties.getVersion() : version);
		return info;
	}

	private Contact createContactFromProperties(final InfoProperties infoProperties) {
		ContactProperties contactProperties = infoProperties.getContact();

		Contact contact = new Contact();
		contact.setName(contactProperties.getName());
		contact.setEmail(contactProperties.getEmail());
		contact.setUrl(resolveUrl(contactProperties.getUrl()));
		return contact.getName() != null || contact.getEmail() != null || contact.getUrl() != null ? contact : null;
	}

	private License createLicenseFromProperties(final InfoProperties infoProperties) {
		LicenseProperties licenseProperties = infoProperties.getLicense();

		License license = new License();
		license.setName(licenseProperties.getName());
		license.setUrl(resolveUrl(licenseProperties.getUrl()));
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
				.filter(server -> server.getUrl() != null)
				.collect(toList());
	}

	public ExternalDocumentation createExternalDocsFromProperties() {
		return createExternalDocsFromProperties(properties.getExternalDocs());
	}

	public ExternalDocumentation createExternalDocsFromProperties(final ExternalDocsProperties props) {
		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setDescription(props.getDescription());
		externalDocs.setUrl(resolveUrl(props.getUrl()));
		return externalDocs.getUrl() != null ? externalDocs : null;
	}

	public Map<String, SecurityScheme> createSecuritySchemesFromProperties() {
		Map<String, SecurityScheme> map = new TreeMap<>();
		for (Entry<String, SecuritySchemeProperties> entry : properties.getSecuritySchemes().entrySet()) {
			SecuritySchemeProperties property = entry.getValue();

			if (Type.http.name().equalsIgnoreCase(property.getType())) {
				SecuritySchemeHttp scheme = new SecuritySchemeHttp();
				scheme.setType(Type.http);
				scheme.setScheme(property.getScheme());
				scheme.setDescription(property.getDescription());
				map.put(entry.getKey(), scheme);
			}
			// TODO add

		}
		return map;
	}

	public String getDescriptionForTag(final String tag) {
		TagProperties tagProperties = properties.getTags().getOrDefault(tag, null);
		return tagProperties != null ? tagProperties.getDescription() : "";
	}

	public ExternalDocumentation getExternalDocumentationForTag(final String tag) {
		TagProperties tagProperties = properties.getTags().getOrDefault(tag, null);
		if (tagProperties != null) {
			return createExternalDocsFromProperties(tagProperties.getExternalDocs());
		}
		return null;
	}

	public ParserProperties getParserProperties() {
		return properties.getParser();
	}

	private String resolveUrl(final URL url) {
		return url != null ? url.toString() : null;
	}

}
