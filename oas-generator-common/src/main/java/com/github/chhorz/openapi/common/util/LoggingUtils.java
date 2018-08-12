
package com.github.chhorz.openapi.common.util;

import com.github.chhorz.openapi.common.properties.ParserProperties;

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
