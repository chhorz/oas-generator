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

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.Objects;

import static java.lang.String.format;
import static java.lang.String.join;

/**
 * Internal logging utility class.
 *
 * @author chhorz
 */
public class LogUtils {

	public static final String OPENAPI_LOG_GROUP = "[OpenAPI]";

	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";

	private final Messager messager;
	private final ParserProperties properties;

	private final String componentLogGroup;

	/**
	 * Creates a new {@link LogUtils} instance.
	 *
	 * @param messager the messager from the {@link javax.annotation.processing.ProcessingEnvironment}
	 * @param properties the given parser properties that are required for the log level
	 */
	public LogUtils(final Messager messager, final ParserProperties properties) {
		this(messager, properties, "");
	}

	/**
	 * Creates a new {@link LogUtils} instance.
	 *
	 * @param messager the messager from the {@link javax.annotation.processing.ProcessingEnvironment}
	 * @param properties the given parser properties that are required for the log level
	 * @param componentLogGroup an mandatory sub-group for the log statements
	 */
	public LogUtils(final Messager messager, final ParserProperties properties, final String componentLogGroup) {
		Objects.requireNonNull(properties, "The properties must not be null.");
		Objects.requireNonNull(componentLogGroup, "The logging group component must not be null.");

		this.messager = messager;
		this.properties = properties;
		this.componentLogGroup = componentLogGroup.trim();
	}

	/**
	 * Creates a cloned {@code LogUtils} class with the given component log group.
	 *
	 * @param componentLogGroup the sub-group for the log statements
	 * @return a new {@link LogUtils} class
	 */
	public LogUtils configureWithComponent(final String componentLogGroup){
		return new LogUtils(messager, properties, componentLogGroup);
	}

	/**
	 * Prints the given log message if the log level is {@value DEBUG}.
	 * <p>
	 * The parameter are related to {@link String#format(String, Object...)}.
	 *
	 * @param format the format string
	 * @param arguments arguments for the format string
	 */
	public void logDebug(final String format, final Object... arguments) {
		if (DEBUG.equalsIgnoreCase(properties.getLogLevel())) {
			log(format, arguments);
		}
	}

	/**
	 * Prints the given log message if the log level is not {@value ERROR}.
	 * <p>
	 * The parameter are related to {@link String#format(String, Object...)}.
	 *
	 * @param format the format string
	 * @param arguments arguments for the format string
	 */
	public void logInfo(final String format, final Object... arguments) {
		if (!ERROR.equalsIgnoreCase(properties.getLogLevel())) {
			log(format, arguments);
		}
	}

	/**
	 * Prints the given log message.
	 * <p>
	 * The parameter are related to {@link String#format(String, Object...)}.
	 *
	 * @param format the format string
	 * @param arguments arguments for the format string
	 */
	public void logError(final String format, final Object... arguments) {
		log(format, arguments);
	}

	/**
	 * Prints the given log message.
	 * <p>
	 * The parameter are related to {@link String#format(String, Object...)}.
	 *
	 * @param format the format string
	 * @param exception the current exception (will be logged via {@link Exception#printStackTrace()})
	 * @param arguments arguments for the format string
	 */
	public void logError(final String format, final Exception exception, final Object... arguments) {
		log(format, exception, arguments);
	}

	private void log(final String format, final Object... arguments) {
		String message = format("%s %s", join(" ", OPENAPI_LOG_GROUP, componentLogGroup).trim(), format(format, arguments));

		if (messager != null) {
			messager.printMessage(Diagnostic.Kind.NOTE, message);
		} else {
			System.out.println(message);
		}
	}

	private void log(final String format, final Exception exception, final Object... arguments) {
		String message = format("%s %s (%s)", join(" ", OPENAPI_LOG_GROUP, componentLogGroup).trim(), format(format, arguments), exception.getMessage());

		if (messager != null) {
			messager.printMessage(Diagnostic.Kind.WARNING, message);
		} else {
			System.out.println(message);
		}
		exception.printStackTrace();
	}

}
