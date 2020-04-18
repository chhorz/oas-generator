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

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.SpecificationViolationException;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.domain.SecurityScheme.Type;
import com.github.chhorz.openapi.common.properties.domain.*;
import com.github.chhorz.openapi.common.util.LoggingUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Utility class to load property file into domain objects.
 *
 * @author chhorz
 */
public class GeneratorPropertyLoader {

	private final LoggingUtils log;

	private final Map<String, String> processorOptions;

	private GeneratorProperties properties;

	public GeneratorPropertyLoader(final Map<String, String> processorOptions) {
		this.processorOptions = processorOptions;

		final ParserProperties dummyProperties = new ParserProperties();
		dummyProperties.setLogLevel("INFO");

		this.log = new LoggingUtils(dummyProperties);
		loadProperties();
	}

	private void loadProperties() {

		URL resourceLocation;
		if (processorOptions.get("propertiesPath") == null) {
			log.info("Using default properties location");
			resourceLocation = GeneratorPropertyLoader.class.getClassLoader().getResource("oas-generator.yml");
		} else {
			log.info("Using custom properties location");
			resourceLocation = GeneratorPropertyLoader.class.getClassLoader()
				.getResource(processorOptions.get("propertiesPath"));
		}

		try {
			Yaml yaml = new Yaml(new Constructor(GeneratorProperties.class));
			properties = yaml.load(resourceLocation.openStream());

			log.info("Loaded properties (Path: %s)", resourceLocation.getPath());
		} catch (Exception e) {
			properties = new GeneratorProperties();
			log.info("Using default properties");
		}
	}

	public Info createInfoFromProperties() {
		InfoProperties infoProperties = properties.getInfo();

		String version = processorOptions.getOrDefault(OpenAPIConstants.OPTION_VERSION, null);

		if (infoProperties.getTitle() == null) {
			throw new SpecificationViolationException("Missing 'title' property for 'Info' object.");
		}

		if (version == null && infoProperties.getVersion() == null) {
			throw new SpecificationViolationException("Missing 'version' property for 'Info' object.");
		}

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

		if (contactProperties != null) {
			Contact contact = new Contact();
			contact.setName(contactProperties.getName());
			contact.setEmail(contactProperties.getEmail());
			contact.setUrl(resolveUrl(contactProperties.getUrl()));

			return contact.getName() != null || contact.getEmail() != null || contact.getUrl() != null ? contact : null;
		} else {
			return null;
		}
	}

	private License createLicenseFromProperties(final InfoProperties infoProperties) {
		LicenseProperties licenseProperties = infoProperties.getLicense();

		if (licenseProperties != null) {
			if (licenseProperties.getName() == null) {
				throw new SpecificationViolationException("Missing name for the 'License' object.");
			}

			License license = new License();
			license.setName(licenseProperties.getName());
			license.setUrl(resolveUrl(licenseProperties.getUrl()));
			return license;
		} else {
			return null;
		}
	}

	public List<Server> createServerFromProperties() {
		if (properties.getServers() != null) {
			return properties.getServers()
				.stream()
				.map(s -> {
					if (s.getUrl() == null) {
						throw new SpecificationViolationException("Missing url for the 'Server' object.");
					}

					Server server = new Server();
					server.setDescription(s.getDescription());
					server.setUrl(s.getUrl());
					if (s.getVariables() != null) {
						s.getVariables()
							.forEach((variable, variableObject) -> server.addVariable(variable, createServerVariable(variableObject)));
					}
					return server;
				})
				.collect(toList());
		} else {
			return null;
		}
	}

	private ServerVariableObject createServerVariable(ServerVariableProperties properties) {
		if (properties == null) {
			throw new SpecificationViolationException("Missing information for the 'ServerVariableObject'.");
		} else if (properties.getDefaultValue() == null) {
			throw new SpecificationViolationException("Missing description for 'ServerVariableObject'.");
		}

		ServerVariableObject variableObject = new ServerVariableObject();
		variableObject.setEnumValue(properties.getEnumValues());
		variableObject.setDefaultValue(properties.getDefaultValue());
		variableObject.setDescription(properties.getDescription());
		return variableObject;
	}

	public ExternalDocumentation createExternalDocsFromProperties() {
		ExternalDocsProperties externalDocsProperties = properties.getExternalDocs();

		if (externalDocsProperties != null) {
			return createExternalDocumentation(externalDocsProperties).orElse(null);
		} else {
			return null;
		}
	}

	private Optional<ExternalDocumentation> createExternalDocumentation(ExternalDocsProperties externalDocsProperties) {
		if (externalDocsProperties.getUrl() == null && externalDocsProperties.getDescription() != null) {
			throw new SpecificationViolationException("Missing 'url' property for 'ExternalDocumentation'.");
		}

		ExternalDocumentation externalDocs = new ExternalDocumentation();
		externalDocs.setDescription(externalDocsProperties.getDescription());
		externalDocs.setUrl(resolveUrl(externalDocsProperties.getUrl()));

		if (externalDocs.getUrl() != null) {
			return Optional.of(externalDocs);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Map<String, SecurityScheme>> createSecuritySchemesFromProperties() {
		Map<String, SecurityScheme> map = new TreeMap<>();
		for (Entry<String, SecuritySchemeProperties> entry : properties.getSecuritySchemes().entrySet()) {
			SecuritySchemeProperties property = entry.getValue();

			if (Stream.of(Type.values()).map(Type::name).noneMatch(type -> type.equals(property.getType()))) {
				throw new SpecificationViolationException("Security type must be one of " + Stream.of(Type.values()).map(Type::name).collect(joining(",")));
			}

			if (Type.http.name().equalsIgnoreCase(property.getType())) {
				SecuritySchemeHttp scheme = new SecuritySchemeHttp();
				scheme.setType(Type.http);
				scheme.setScheme(property.getScheme());
				scheme.setDescription(property.getDescription());
				map.put(entry.getKey(), scheme);
			}

			// TODO add other security schemes

		}
		return map.isEmpty() ? Optional.empty() : Optional.of(map);
	}

	public String getDescriptionForTag(final String tag) {
		TagProperties tagProperties = properties.getTags().getOrDefault(tag, null);
		return tagProperties != null ? tagProperties.getDescription() : null;
	}

	public ExternalDocumentation getExternalDocumentationForTag(final String tag) {
		TagProperties tagProperties = properties.getTags().getOrDefault(tag, null);
		if (tagProperties != null) {
			return createExternalDocumentation(tagProperties.getExternalDocs()).orElse(null);
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
