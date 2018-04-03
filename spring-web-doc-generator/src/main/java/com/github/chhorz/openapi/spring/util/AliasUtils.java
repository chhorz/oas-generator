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
		RequestMapping mapping = null;

		if (executableElement.getAnnotation(RequestMapping.class) != null) {
			mapping = executableElement.getAnnotation(RequestMapping.class);
		} else if (executableElement.getAnnotation(GetMapping.class) != null) {
			mapping = convertSpecificMapping(executableElement.getAnnotation(GetMapping.class));
		} else if (executableElement.getAnnotation(PostMapping.class) != null) {
			mapping = convertSpecificMapping(executableElement.getAnnotation(PostMapping.class));
		} else if (executableElement.getAnnotation(PutMapping.class) != null) {
			mapping = convertSpecificMapping(executableElement.getAnnotation(PutMapping.class));
		} else if (executableElement.getAnnotation(DeleteMapping.class) != null) {
			mapping = convertSpecificMapping(executableElement.getAnnotation(DeleteMapping.class));
		} else if (executableElement.getAnnotation(PatchMapping.class) != null) {
			mapping = convertSpecificMapping(executableElement.getAnnotation(PatchMapping.class));
		}

		return mapping;
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

	private RequestMapping convertSpecificMapping(final PostMapping postMapping) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return postMapping.annotationType();
			}

			@Override
			public String[] value() {
				return postMapping.value();
			}

			@Override
			public String[] produces() {
				return postMapping.produces();
			}

			@Override
			public String[] path() {
				return postMapping.path();
			}

			@Override
			public String[] params() {
				return postMapping.params();
			}

			@Override
			public String name() {
				return postMapping.name();
			}

			@Override
			public RequestMethod[] method() {
				return new RequestMethod[] { RequestMethod.POST };
			}

			@Override
			public String[] headers() {
				return postMapping.headers();
			}

			@Override
			public String[] consumes() {
				return postMapping.consumes();
			}
		};
	}

	private RequestMapping convertSpecificMapping(final PutMapping putMapping) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return putMapping.annotationType();
			}

			@Override
			public String[] value() {
				return putMapping.value();
			}

			@Override
			public String[] produces() {
				return putMapping.produces();
			}

			@Override
			public String[] path() {
				return putMapping.path();
			}

			@Override
			public String[] params() {
				return putMapping.params();
			}

			@Override
			public String name() {
				return putMapping.name();
			}

			@Override
			public RequestMethod[] method() {
				return new RequestMethod[] { RequestMethod.PUT };
			}

			@Override
			public String[] headers() {
				return putMapping.headers();
			}

			@Override
			public String[] consumes() {
				return putMapping.consumes();
			}
		};
	}

	private RequestMapping convertSpecificMapping(final DeleteMapping deleteMapping) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return deleteMapping.annotationType();
			}

			@Override
			public String[] value() {
				return deleteMapping.value();
			}

			@Override
			public String[] produces() {
				return deleteMapping.produces();
			}

			@Override
			public String[] path() {
				return deleteMapping.path();
			}

			@Override
			public String[] params() {
				return deleteMapping.params();
			}

			@Override
			public String name() {
				return deleteMapping.name();
			}

			@Override
			public RequestMethod[] method() {
				return new RequestMethod[] { RequestMethod.DELETE };
			}

			@Override
			public String[] headers() {
				return deleteMapping.headers();
			}

			@Override
			public String[] consumes() {
				return deleteMapping.consumes();
			}
		};
	}

	private RequestMapping convertSpecificMapping(final PatchMapping patchMapping) {
		return new RequestMapping() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return patchMapping.annotationType();
			}

			@Override
			public String[] value() {
				return patchMapping.value();
			}

			@Override
			public String[] produces() {
				return patchMapping.produces();
			}

			@Override
			public String[] path() {
				return patchMapping.path();
			}

			@Override
			public String[] params() {
				return patchMapping.params();
			}

			@Override
			public String name() {
				return patchMapping.name();
			}

			@Override
			public RequestMethod[] method() {
				return new RequestMethod[] { RequestMethod.PATCH };
			}

			@Override
			public String[] headers() {
				return patchMapping.headers();
			}

			@Override
			public String[] consumes() {
				return patchMapping.consumes();
			}
		};
	}

}
