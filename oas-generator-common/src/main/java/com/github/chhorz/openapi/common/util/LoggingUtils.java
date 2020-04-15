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

package com.github.chhorz.openapi.common.util;

import com.github.chhorz.openapi.common.properties.domain.ParserProperties;

import java.util.Objects;

public class LoggingUtils {

	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";

	private ParserProperties properties;
	private String component;

	public LoggingUtils(final ParserProperties properties) {
		this(properties, "");
	}

	public LoggingUtils(final ParserProperties properties, final String component){
		Objects.requireNonNull(component, "The logging component must not be null.");

		this.properties = properties;

		if (component.isEmpty()) {
			this.component = "";
		} else {
			this.component = String.format("%s ",component.trim());
		}
	}

	public void debug(final String format, final Object... arguments) {
		if (DEBUG.equalsIgnoreCase(properties.getLogLevel())) {
			log(format, arguments);
		}
	}

	public void info(final String format, final Object... arguments) {
		if (!ERROR.equalsIgnoreCase(properties.getLogLevel())) {
			log(format, arguments);
		}
	}

	public void error(final String format, final Object... arguments) {
		log(format, arguments);
	}

	public void error(final String format, final Exception exception) {
		log(format, exception);
	}

	private void log(final String format, final Object... arguments) {
		String message = new StringBuilder()
				.append("[OpenAPI] ")
				.append(component)
				.append(String.format(format, arguments))
				.toString();
		System.out.println(message);
	}

}
