package com.github.chhorz.openapi.spring.util;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.lang.model.element.ExecutableElement;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	public RequestMapping getMappingAnnotation(final ExecutableElement executableElement) {
		if (executableElement.getAnnotation(RequestMapping.class) != null) {
			return executableElement.getAnnotation(RequestMapping.class);
		} else if (executableElement.getAnnotation(GetMapping.class) != null) {
			return convertSpecificMapping(executableElement.getAnnotation(GetMapping.class));
		} else if (executableElement.getAnnotation(PostMapping.class) != null) {

		} else if (executableElement.getAnnotation(PutMapping.class) != null) {

		} else if (executableElement.getAnnotation(DeleteMapping.class) != null) {

		} else if (executableElement.getAnnotation(PatchMapping.class) != null) {

		}

		return null;
	}

	private RequestMapping convertSpecificMapping(final GetMapping getMapping) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return getMapping.annotationType();
			}

			@Override
			public String[] value() {
				return getMapping.value();
			}

			@Override
			public String[] produces() {
				return getMapping.produces();
			}

			@Override
			public String[] path() {
				return getMapping.path();
			}

			@Override
			public String[] params() {
				return getMapping.params();
			}

			@Override
			public String name() {
				return getMapping.name();
			}

			@Override
			public RequestMethod[] method() {
				return new RequestMethod[] { RequestMethod.GET };
			}

			@Override
			public String[] headers() {
				return getMapping.headers();
			}

			@Override
			public String[] consumes() {
				return getMapping.consumes();
			}
		};
	}

}
