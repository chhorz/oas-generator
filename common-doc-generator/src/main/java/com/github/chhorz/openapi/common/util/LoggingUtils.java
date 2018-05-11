
package com.github.chhorz.openapi.common.util;

import com.github.chhorz.openapi.common.properties.ParserProperties;

public class LoggingUtils {

	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";

	private ParserProperties properties;

	public LoggingUtils(final ParserProperties properties) {
		this.properties = properties;
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

	public void error(final String message, final Object... arguments) {
		log(message, arguments);
	}

	public void error(final String message, final Exception exception) {
		log(message, exception);
	}

	private void log(final String format, final Object... arguments) {
		String message = new StringBuilder()
				.append("[OpenAPI] ")
				.append(String.format(format, arguments))
				.toString();
		System.out.println(message);
	}

}
