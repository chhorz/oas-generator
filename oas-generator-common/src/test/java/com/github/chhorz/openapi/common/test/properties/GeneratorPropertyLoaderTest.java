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
package com.github.chhorz.openapi.common.test.properties;

import com.github.chhorz.openapi.common.SpecificationViolationException;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.github.chhorz.openapi.common.test.properties.test.ProcessorAProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratorPropertyLoaderTest {

	@Nested
	@DisplayName("Parser enabled tests")
	@GitHubIssue("#53")
	class ParserEnabledTest {

		@Test
		void testParserDisabled() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/parserDisabled.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

			// then
			assertThat(parserProperties)
				.isNotNull()
				.hasFieldOrPropertyWithValue("enabled", false);
		}

		@Test
		void testParserInvalidValue() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/parserEnabledInvalid.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

			// then
			assertThat(parserProperties)
				.isNotNull()
				.hasFieldOrPropertyWithValue("enabled", true); // default properties
		}

	}

	@Nested
	@DisplayName("Info properties test")
	class InfoPropertiesTest {

		@Test
		void testInfoProperties() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/info.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Info info = generatorPropertyLoader.createInfoFromProperties();

			// then
			assertThat(info)
				.isNotNull()
				.hasFieldOrPropertyWithValue("title", "MyService")
				.hasFieldOrPropertyWithValue("version", "1.2.3-SNAPSHOT")
				.hasNoNullFieldsOrPropertiesExcept("description", "termsOfService", "xGeneratedBy", "xGeneratedTs");

			assertThat(info.getContact())
				.isNotNull()
				.hasFieldOrPropertyWithValue("name", "John Doe")
				.hasFieldOrPropertyWithValue("url", "https://www.google.com")
				.hasFieldOrPropertyWithValue("email", "john@doe.com");

			assertThat(info.getLicense())
				.isNotNull()
				.hasFieldOrPropertyWithValue("name", "Apache License, Version 2.0")
				.hasFieldOrPropertyWithValue("url", "https://www.apache.org/licenses/LICENSE-2.0");
		}

		@Test
		void testEmptyInfoProperties() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/infoEmpty.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when - then
			assertThatThrownBy(generatorPropertyLoader::createInfoFromProperties)
				.isInstanceOf(SpecificationViolationException.class)
				.hasMessage("Missing 'Info' object.");
		}

	}

	@Nested
	@DisplayName("Test for post processor properties")
	class PostProcessorPropertiesTest {

		@Test
		void testPostProcessorProperties() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/postProcessorTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

			// then
			assertThat(parserProperties)
				.isNotNull();

			assertThat(parserProperties.getPostProcessor("one", ProcessorAProperties.class))
				.isNotNull()
				.isNotEmpty()
				.get()
				.hasFieldOrPropertyWithValue("valueOne", "Test")
				.hasFieldOrPropertyWithValue("valueTwo", 2);
		}

		@Test
		@GitHubIssue("#3")
		void testEmptyPostProcessorProperties() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/postProcessorEmptyTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			ParserProperties parserProperties = generatorPropertyLoader.getParserProperties();

			// then
			assertThat(parserProperties)
				.isNotNull();

			Optional<ProcessorAProperties> optionalProcessorAProperties = parserProperties.getPostProcessor("one", ProcessorAProperties.class);

			assertThat(optionalProcessorAProperties)
				.isNotNull()
				.isPresent()
				.get()
				.hasFieldOrPropertyWithValue("valueTwo", 0);
		}

	}

	@Nested
	@DisplayName("Tags from Properties")
	class TagPropertiesTest {

		@Test
		void testTagWithExternalDocumentation() {
			// given
			String tagName = "tag_a";

			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/tagTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			String tagDescription = generatorPropertyLoader.getDescriptionForTag(tagName);
			ExternalDocumentation externalDocumentationForTag = generatorPropertyLoader.getExternalDocumentationForTag(tagName);

			// then
			assertThat(tagDescription)
				.isNotNull()
				.isEqualTo("Lorem ipsum");

			assertThat(externalDocumentationForTag)
				.isNotNull()
				.hasFieldOrPropertyWithValue("url", "https://www.google.com")
				.hasAllNullFieldsOrPropertiesExcept("url");
		}

		@Test
		@GitHubIssue("#9")
		void testTagWithoutExternalDocumentation() {
			// given
			String tagName = "tag_b";

			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/tagTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			String tagDescription = generatorPropertyLoader.getDescriptionForTag(tagName);
			ExternalDocumentation externalDocumentationForTag = generatorPropertyLoader.getExternalDocumentationForTag(tagName);

			// then
			assertThat(tagDescription)
				.isNotNull()
				.isEqualTo("Lorem ipsum");

			assertThat(externalDocumentationForTag)
				.isNull();
		}

		@Test
		@GitHubIssue("#31")
		void testEmptyTag() {
			// given
			String tagName = "test";

			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/tagEmptyTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			String tagDescription = generatorPropertyLoader.getDescriptionForTag(tagName);
			ExternalDocumentation externalDocumentationForTag = generatorPropertyLoader.getExternalDocumentationForTag(tagName);

			// then
			assertThat(tagDescription)
				.isNull();

			assertThat(externalDocumentationForTag)
				.isNull();
		}

	}

	@Nested
	@GitHubIssue("#19")
	@DisplayName("Security Schemes from Properties")
	class SecuritySchemesTest {

		@Test
		@DisplayName("Invalid type")
		void testInvalidSecurityType() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityInvalidType.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when - then
			assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
				.isInstanceOf(SpecificationViolationException.class)
				.hasMessage("Security type must be one of apiKey,http,oauth2,openIdConnect");
		}

		@Test
		@DisplayName("Empty security object")
		void testEmptySecurity() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityEmptyTest.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

			// then
			assertThat(securitySchemesFromProperties)
				.isNotNull()
				.isNotPresent();
		}

		@Test
		@DisplayName("Basic Authentication")
		void testHttpBasicSecurity() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityBasicHttp.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

			// then
			assertThat(securitySchemesFromProperties)
				.isNotNull()
				.isPresent();

			assertThat(securitySchemesFromProperties.get())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("read_role");

			assertThat(securitySchemesFromProperties.get().get("read_role"))
				.isNotNull()
				.hasFieldOrPropertyWithValue("description", "Basic LDAP read role.")
				.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.http)
				.hasFieldOrPropertyWithValue("scheme", "basic");
		}

		@Test
		@DisplayName("API Key")
		void testApiKeySecurity() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityApiKey.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

			// then
			assertThat(securitySchemesFromProperties)
				.isNotNull()
				.isPresent();

			assertThat(securitySchemesFromProperties.get())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("api_key_name");

			assertThat(securitySchemesFromProperties.get().get("api_key_name"))
				.isNotNull()
				.hasFieldOrPropertyWithValue("description", "Authentication by api key")
				.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.apiKey)
				.hasFieldOrPropertyWithValue("name", "api_key")
				.hasFieldOrPropertyWithValue("in", SecuritySchemeApiKey.In.header);
		}

		@Test
		@DisplayName("API key with invalid IN value")
		void testApiKeySecurityWithInvalidInValue() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityApiKeyInvalidInValue.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when - then
			assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
				.isInstanceOf(SpecificationViolationException.class)
				.hasMessage("Security property 'in' must be one of query,header,cookie");
		}

		@Test
		@DisplayName("JWT Bearer")
		void testHttpBearerSecurity() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityJwtBearer.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

			// then
			assertThat(securitySchemesFromProperties)
				.isNotNull()
				.isPresent();

			assertThat(securitySchemesFromProperties.get())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("bearer_example");

			assertThat(securitySchemesFromProperties.get().get("bearer_example"))
				.isNotNull()
				.hasFieldOrPropertyWithValue("description", "Basic LDAP read role.")
				.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.http)
				.hasFieldOrPropertyWithValue("scheme", "bearer")
				.hasFieldOrPropertyWithValue("bearerFormat", "JWT");
		}

		@Test
		@DisplayName("OpenId Connect")
		void testOpenIdConnectSecurity() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOpenIdConnect.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when
			Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

			// then
			assertThat(securitySchemesFromProperties)
				.isNotNull()
				.isPresent();

			assertThat(securitySchemesFromProperties.get())
				.isNotNull()
				.isNotEmpty()
				.hasSize(1)
				.containsOnlyKeys("open_id_connect_name");

			assertThat(securitySchemesFromProperties.get().get("open_id_connect_name"))
				.isNotNull()
				.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.openIdConnect)
				.hasFieldOrPropertyWithValue("openIdConnectUrl", "https://www.google.com");
		}

		@Test
		@DisplayName("OpenId Connect with invalid URL")
		void testOpenIdConnectSecurityWithInvalidUrl() {
			// given
			Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOpenIdConnectInvalidUrl.yml");
			GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

			// when - then
			assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
				.isInstanceOf(SpecificationViolationException.class)
				.hasMessage("Security property 'openIdUrl' is not a valid URL");
		}

		@Nested
		@DisplayName("OAuth2")
		class OAuth2SecuritySchemesTest {

			@Test
			@DisplayName("Implicit")
			void testOauth2Implicit() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2Implicit.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

				// when
				Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

				// then
				assertThat(securitySchemesFromProperties)
					.isNotNull()
					.isPresent();

				assertThat(securitySchemesFromProperties.get())
					.isNotNull()
					.isNotEmpty()
					.hasSize(1)
					.containsOnlyKeys("oauth_implicit")
					.extractingByKey("oauth_implicit")
					.isNotNull()
					.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.oauth2)
					.isInstanceOfSatisfying(SecuritySchemeOAuth2.class, oauth2 -> assertThat(oauth2.getFlows())
						.hasAllNullFieldsOrPropertiesExcept("implicit")
						.extracting(OAuthFlows::getImplicit)
						.hasFieldOrPropertyWithValue("authorizationUrl", "https://www.openapis.org")
						.hasFieldOrPropertyWithValue("refreshUrl", "https://openapis.org")
						.hasFieldOrPropertyWithValue("scopes", singletonMap("write:test", "Lorem ipsum")));
			}

			@Test
			@DisplayName("Implicit with invalid URL")
			void testOauth2ImplicitWithInvalidUrl() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2ImplicitInvalidUrl.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);


				// when - then
				assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
					.isInstanceOf(SpecificationViolationException.class)
					.hasMessage("Security property 'authorizationUrl' is not a valid URL");
			}

			@Test
			@DisplayName("Authorization Code")
			void testOauth2AuthorizationCode() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2AuthorizationCode.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

				// when
				Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

				// then
				assertThat(securitySchemesFromProperties)
					.isNotNull()
					.isPresent();

				assertThat(securitySchemesFromProperties.get())
					.isNotNull()
					.isNotEmpty()
					.hasSize(1)
					.containsOnlyKeys("oauth_auth_code")
					.extractingByKey("oauth_auth_code")
					.isNotNull()
					.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.oauth2)
					.isInstanceOfSatisfying(SecuritySchemeOAuth2.class, oauth2 -> assertThat(oauth2.getFlows())
						.hasAllNullFieldsOrPropertiesExcept("authorizationCode")
						.extracting(OAuthFlows::getAuthorizationCode)
						.hasFieldOrPropertyWithValue("authorizationUrl", "https://www.openapis.org")
						.hasFieldOrPropertyWithValue("refreshUrl", "https://openapis.org")
						.hasFieldOrPropertyWithValue("tokenUrl", "https://openapis.org")
						.hasFieldOrPropertyWithValue("scopes", singletonMap("write:test", "Lorem ipsum")));
			}

			@Test
			@DisplayName("Authorization Code with invalid URL")
			void testOauth2AuthorizationCodeWithInvalidUrl() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2AuthorizationCodeInvalidUrl.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);


				// when - then
				assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
					.isInstanceOf(SpecificationViolationException.class)
					.hasMessage("Security property 'tokenUrl' is not a valid URL");
			}

			@Test
			@DisplayName("Client Credentials")
			void testOauth2ClientCredentials() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2ClientCredentials.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

				// when
				Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

				// then
				assertThat(securitySchemesFromProperties)
					.isNotNull()
					.isPresent();

				assertThat(securitySchemesFromProperties.get())
					.isNotNull()
					.isNotEmpty()
					.hasSize(1)
					.containsOnlyKeys("oauth_client_credentials")
					.extractingByKey("oauth_client_credentials")
					.isNotNull()
					.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.oauth2)
					.isInstanceOfSatisfying(SecuritySchemeOAuth2.class, oauth2 -> assertThat(oauth2.getFlows())
						.hasAllNullFieldsOrPropertiesExcept("clientCredentials")
						.extracting(OAuthFlows::getClientCredentials)
						.hasFieldOrPropertyWithValue("refreshUrl", "https://openapis.org")
						.hasFieldOrPropertyWithValue("tokenUrl", "https://openapis.org")
						.extracting(OAuthFlow::getScopes)
						.isInstanceOfSatisfying(Map.class, scopes -> {
							assertThat(scopes)
								.isNotNull()
								.hasSize(2)
								.containsEntry("write:test", "Lorem ipsum")
								.containsEntry("read:test", "Another test");
						}));
			}

			@Test
			@DisplayName("Client Credentials with invalid URL")
			void testOauth2ClientCredentialsWithInvalidUrl() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2ClientCredentialsInvalidUrl.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);


				// when - then
				assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
					.isInstanceOf(SpecificationViolationException.class)
					.hasMessage("Security property 'tokenUrl' is not a valid URL");
			}


			@Test
			@DisplayName("Password")
			void testOauth2Password() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2Password.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);

				// when
				Optional<Map<String, SecurityScheme>> securitySchemesFromProperties = generatorPropertyLoader.createSecuritySchemesFromProperties();

				// then
				assertThat(securitySchemesFromProperties)
					.isNotNull()
					.isPresent();

				assertThat(securitySchemesFromProperties.get())
					.isNotNull()
					.isNotEmpty()
					.hasSize(1)
					.containsOnlyKeys("oauth_password")
					.extractingByKey("oauth_password")
					.isNotNull()
					.hasFieldOrPropertyWithValue("type", SecurityScheme.Type.oauth2)
					.isInstanceOfSatisfying(SecuritySchemeOAuth2.class, oauth2 -> assertThat(oauth2.getFlows())
						.hasAllNullFieldsOrPropertiesExcept("password")
						.extracting(OAuthFlows::getPassword)
						.hasFieldOrPropertyWithValue("refreshUrl", "https://openapis.org")
						.hasFieldOrPropertyWithValue("tokenUrl", "https://www.openapis.org")
						.hasFieldOrPropertyWithValue("scopes", singletonMap("write:test", "Lorem ipsum")));
			}

			@Test
			@DisplayName("Password with invalid URL")
			void testOauth2PasswordWithInvalidUrl() {
				// given
				Map<String, String> processorOptions = singletonMap("propertiesPath", "./properties/securityOauth2PasswordInvalidUrl.yml");
				GeneratorPropertyLoader generatorPropertyLoader = new GeneratorPropertyLoader(processorOptions);


				// when - then
				assertThatThrownBy(generatorPropertyLoader::createSecuritySchemesFromProperties)
					.isInstanceOf(SpecificationViolationException.class)
					.hasMessage("Security property 'tokenUrl' is not a valid URL");
			}

		}

	}

}
