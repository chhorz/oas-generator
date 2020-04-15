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
package com.github.chhorz.openapi.common.properties.domain;

import com.github.chhorz.openapi.common.properties.GeneratorPropertiesDefaults;

import java.net.URL;

public class ContactProperties {

	private String name;
	private URL url;
	private String email;

	public ContactProperties() {
		name = GeneratorPropertiesDefaults.CONTACT_NAME;
		url = GeneratorPropertiesDefaults.CONTACT_URL;
		email = GeneratorPropertiesDefaults.CONTACT_EMAIL;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(final URL url) {
		this.url = url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

}
