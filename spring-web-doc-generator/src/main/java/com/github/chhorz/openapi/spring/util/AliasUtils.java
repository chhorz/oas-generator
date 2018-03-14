package com.github.chhorz.openapi.spring.util;

import java.util.function.Function;

public class AliasUtils<T> {

	public String getValue(final T t, final Function<T, String> primary, final Function<T, String> secondary,
			final String defaultValue) {
		if (primary.apply(t).isEmpty() && secondary.apply(t).isEmpty()) {
			return defaultValue;
		} else {
			if (!primary.apply(t).isEmpty()) {
				return primary.apply(t);
			} else {
				return secondary.apply(t);
			}
		}
	}

}
