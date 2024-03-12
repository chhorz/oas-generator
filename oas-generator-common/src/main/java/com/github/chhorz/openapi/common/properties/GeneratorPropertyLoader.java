/**
 *
 *    Copyright 2018-2023 the original author or authors.
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
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.domain.SecurityScheme.Type;
import com.github.chhorz.openapi.common.domain.SecuritySchemeApiKey.In;
import com.github.chhorz.openapi.common.exception.SpecificationViolationException;
import com.github.chhorz.openapi.common.properties.domain.*;
import com.github.chhorz.openapi.common.util.LogUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.processing.Messager;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Utility class to load property file into domain objects.
 *
 * @author chhorz
 */
public class GeneratorPropertyLoader {

	private final LogUtils logUtils;

	private final Map<String, String> processorOptions;

	private GeneratorProperties properties;
	private Properties versionProperties;

	public GeneratorPropertyLoader(final Messager messager, final Map<String, String> processorOptions) {
		this.processorOptions = processorOptions;

		final ParserProperties dummyProperties = new ParserProperties();
		dummyProperties.setLogLevel("INFO");

		this.logUtils = new LogUtils(messager, dummyProperties);
		loadProperties();
	}

	private void loadProperties() {

		URL resourceLocation;
		if (processorOptions.get("propertiesPath") == null) {
			logUtils.logInfo("Using default properties location");
			resourceLocation = GeneratorPropertyLoader.class.getClassLoader().getResource("oas-generator.yml");
		} else {
			logUtils.logInfo("Using custom properties location");
			resourceLocation = GeneratorPropertyLoader.class.getClassLoader()
				.getResource(processorOptions.get("propertiesPath"));
		}

		try {
			Yaml yaml = new Yaml(new Constructor(GeneratorProperties.class, new LoaderOptions()));

			if (resourceLocation != null) {
				properties = yaml.load(resourceLocation.openStream());
				logUtils.logInfo("Loaded properties (Path: %s)", resourceLocation.getPath());
			} else if (processorOptions.get("propertiesPath") != null) {
				properties = yaml.load(Files.newInputStream(Paths.get(processorOptions.get("propertiesPath"))));
				logUtils.logInfo("Loaded properties (Path: %s)", processorOptions.get("propertiesPath"));
			} else {
				properties = new GeneratorProperties();
				logUtils.logInfo("Using default properties!");
			}

		} catch (Exception e) {
			properties = new GeneratorProperties();
			logUtils.logError("An exception occurred. Using default properties!", e);
		}

		versionProperties = new Properties();
		try {
			versionProperties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
		} catch (Exception e) {
			versionProperties.put("oas-generator.version", "unknown");
			logUtils.logError("An exception occurred. Using default version properties!", e);
		}
	}

	public String getOasGeneratorVersion() {
		return versionProperties.getProperty("oas-generator.version");
	}

	public Info createInfoFromProperties() {
		InfoProperties infoProperties = properties.getInfo();

		String version = processorOptions.getOrDefault(OpenAPIConstants.OPTION_VERSION, null);

		if (infoProperties == null) {
			throw new SpecificationViolationException("Missing 'Info' object.");
		}

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
		Map<String, SecuritySchemeProperties> securitySchemePropertiesMap = properties.getSecuritySchemes();
		if (securitySchemePropertiesMap != null) {
			for (Entry<String, SecuritySchemeProperties> entry : securitySchemePropertiesMap.entrySet()) {
				SecuritySchemeProperties property = entry.getValue();

				if (Stream.of(Type.values()).map(Type::name).noneMatch(type -> type.equals(property.getType()))) {
					throw new SpecificationViolationException("Security type must be one of " + Stream.of(Type.values()).map(Type::name).collect(joining(",")));
				}

				if (Type.http.name().equalsIgnoreCase(property.getType())) {
					SecuritySchemeHttp scheme = new SecuritySchemeHttp();
					scheme.setType(Type.http);
					scheme.setScheme(property.getScheme());
					scheme.setDescription(property.getDescription());

					if ("bearer".equalsIgnoreCase(property.getScheme())) {
						scheme.setBearerFormat(property.getBearerFormat());
					}

					map.put(entry.getKey(), scheme);
				} else if (Type.apiKey.name().equalsIgnoreCase(property.getType())) {
					if (Stream.of(In.values()).map(In::name).noneMatch(in -> in.equals(property.getIn()))) {
						throw new SpecificationViolationException("Security property 'in' must be one of " + Stream.of(In.values()).map(In::name).collect(joining(",")));
					} else if (property.getName() == null || property.getName().isEmpty()) {
						throw new SpecificationViolationException("Security property 'name' must be present");
					}

					SecuritySchemeApiKey scheme = new SecuritySchemeApiKey();
					scheme.setType(Type.apiKey);
					scheme.setIn(In.of(property.getIn()));
					scheme.setName(property.getName());
					scheme.setDescription(property.getDescription());

					map.put(entry.getKey(), scheme);
				} else if (Type.oauth2.name().equalsIgnoreCase(property.getType())) {
					if (property.getFlows() == null) {
						throw new SpecificationViolationException("Security property 'flows' must be present");
					}

					SecuritySchemeOAuthFlowProperties authorizationCode = property.getFlows().getAuthorizationCode();
					if (authorizationCode != null) {
						validateRequiredUrl(authorizationCode.getAuthorizationUrl(), "authorizationUrl");
						validateRequiredUrl(authorizationCode.getTokenUrl(), "tokenUrl");
						validateOptionalUrl(authorizationCode.getRefreshUrl(), "refreshUrl");
						if (authorizationCode.getScopes() == null) {
							throw new SpecificationViolationException("Security property 'scopes' must be present");
						}

						OAuthFlow flow = new OAuthFlow();
						flow.setAuthorizationUrl(authorizationCode.getAuthorizationUrl());
						flow.setTokenUrl(authorizationCode.getTokenUrl());
						flow.setRefreshUrl(authorizationCode.getRefreshUrl());
						flow.setScopes(authorizationCode.getScopes());

						OAuthFlows flows = new OAuthFlows();
						flows.setAuthorizationCode(flow);

						map.put(entry.getKey(), new SecuritySchemeOAuth2(flows));
					}

					SecuritySchemeOAuthFlowProperties clientCredentials = property.getFlows().getClientCredentials();
					if (clientCredentials != null) {
						validateRequiredUrl(clientCredentials.getTokenUrl(), "tokenUrl");
						validateOptionalUrl(clientCredentials.getRefreshUrl(), "refreshUrl");
						if (clientCredentials.getScopes() == null) {
							throw new SpecificationViolationException("Security property 'scopes' must be present");
						}

						OAuthFlow flow = new OAuthFlow();
						flow.setTokenUrl(clientCredentials.getTokenUrl());
						flow.setRefreshUrl(clientCredentials.getRefreshUrl());
						flow.setScopes(clientCredentials.getScopes());

						OAuthFlows flows = new OAuthFlows();
						flows.setClientCredentials(flow);

						map.put(entry.getKey(), new SecuritySchemeOAuth2(flows));
					}

					SecuritySchemeOAuthFlowProperties password = property.getFlows().getPassword();
					if (password != null) {
						validateRequiredUrl(password.getTokenUrl(), "tokenUrl");
						validateOptionalUrl(password.getRefreshUrl(), "refreshUrl");
						if (password.getScopes() == null) {
							throw new SpecificationViolationException("Security property 'scopes' must be present");
						}

						OAuthFlow flow = new OAuthFlow();
						flow.setTokenUrl(password.getTokenUrl());
						flow.setRefreshUrl(password.getRefreshUrl());
						flow.setScopes(password.getScopes());

						OAuthFlows flows = new OAuthFlows();
						flows.setPassword(flow);

						map.put(entry.getKey(), new SecuritySchemeOAuth2(flows));
					}

					SecuritySchemeOAuthFlowProperties implicit = property.getFlows().getImplicit();
					if (implicit != null) {
						validateRequiredUrl(implicit.getAuthorizationUrl(), "authorizationUrl");
						validateOptionalUrl(implicit.getRefreshUrl(), "refreshUrl");
						if (implicit.getScopes() == null) {
							throw new SpecificationViolationException("Security property 'scopes' must be present");
						}

						OAuthFlow flow = new OAuthFlow();
						flow.setAuthorizationUrl(implicit.getAuthorizationUrl());
						flow.setRefreshUrl(implicit.getRefreshUrl());
						flow.setScopes(implicit.getScopes());

						OAuthFlows flows = new OAuthFlows();
						flows.setImplicit(flow);

						map.put(entry.getKey(), new SecuritySchemeOAuth2(flows));
					}

				} else if (Type.openIdConnect.name().equalsIgnoreCase(property.getType())) {
					validateRequiredUrl(property.getOpenIdConnectUrl(), "openIdUrl");

					SecuritySchemeOpenIdConnect scheme = new SecuritySchemeOpenIdConnect();
					scheme.setType(Type.openIdConnect);
					scheme.setOpenIdConnectUrl(property.getOpenIdConnectUrl());
					scheme.setDescription(property.getDescription());

					map.put(entry.getKey(), scheme);
				}
			}
		}
		return map.isEmpty() ? Optional.empty() : Optional.of(map);
	}

	private void validateRequiredUrl(String value, String property){
		if (value == null || value.isEmpty()) {
			throw new SpecificationViolationException("Security property '" + property + "' must be present");
		} else {
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				throw new SpecificationViolationException("Security property '" + property + "' is not a valid URL");
			}
		}
	}

	private void validateOptionalUrl(String value, String property){
		if (value != null) {
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				throw new SpecificationViolationException("Security property '" + property + "' is not a valid URL");
			}
		}
	}

	public String getDescriptionForTag(final String tag) {
		Map<String, TagProperties> tagPropertiesMap = properties.getTags();
		if (tagPropertiesMap != null) {
			TagProperties tagProperties = tagPropertiesMap.getOrDefault(tag, null);
			if (tagProperties != null) {
				return tagProperties.getDescription();
			}
		}
		return null;
	}

	public ExternalDocumentation getExternalDocumentationForTag(final String tag) {
		Map<String, TagProperties> tagPropertiesMap = properties.getTags();
		if (tagPropertiesMap != null) {
			TagProperties tagProperties = tagPropertiesMap.getOrDefault(tag, null);
			if (tagProperties != null) {
				return createExternalDocumentation(tagProperties.getExternalDocs()).orElse(null);
			}
		}
		return null;
	}

	/**
	 * Returns the parser properties from the given configuration file or a new instance with default values
	 * if the parser object is null.
	 *
	 * @return the parser properties
	 */
	public ParserProperties getParserProperties() {
		return properties.getParser() != null ? properties.getParser() : new ParserProperties();
	}

	private String resolveUrl(final URL url) {
		return url != null ? url.toString() : null;
	}

}
