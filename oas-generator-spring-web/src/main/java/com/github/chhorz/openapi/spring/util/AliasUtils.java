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
package com.github.chhorz.openapi.spring.util;

import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class AliasUtils {

	public <T> String getValue(final T t, final Function<T, String> primary, final Function<T, String> secondary,
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

	public RequestMapping getMappingAnnotation(final Element element) {
		RequestMapping mapping = null;

		if (element.getAnnotation(RequestMapping.class) != null) {
			mapping = element.getAnnotation(RequestMapping.class);
		} else if (element.getAnnotation(GetMapping.class) != null) {
			mapping = convertSpecificMapping(element.getAnnotation(GetMapping.class));
		} else if (element.getAnnotation(PostMapping.class) != null) {
			mapping = convertSpecificMapping(element.getAnnotation(PostMapping.class));
		} else if (element.getAnnotation(PutMapping.class) != null) {
			mapping = convertSpecificMapping(element.getAnnotation(PutMapping.class));
		} else if (element.getAnnotation(DeleteMapping.class) != null) {
			mapping = convertSpecificMapping(element.getAnnotation(DeleteMapping.class));
		} else if (element.getAnnotation(PatchMapping.class) != null) {
			mapping = convertSpecificMapping(element.getAnnotation(PatchMapping.class));
		}

		return mapping;
	}

	public RequestMapping mergeClassAndMethodMappings(final RequestMapping classMapping, final RequestMapping methodMapping) {
		if (classMapping == null) {
			return methodMapping;
		} else if (methodMapping == null) {
			return classMapping;
		} else {
			return new RequestMapping() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return methodMapping.annotationType();
				}

				@Override
				public String name() {
					if (classMapping.name().isEmpty()) {
						return methodMapping.name();
					} else if (methodMapping.name().isEmpty()) {
						return classMapping.name();
					} else {
						return String.format("%s#%s", classMapping.name(), methodMapping.name());
					}
				}

				@Override
				public String[] value() {
					String[] classPath = getValue(classMapping, RequestMapping::path, RequestMapping::value, new String[]{""});
					String[] methodPath = getValue(methodMapping, RequestMapping::path, RequestMapping::value, new String[]{""});
					List<String> resolvedPaths = new ArrayList<>();
					Stream.of(classPath).forEach(singleClassPath ->
						Stream.of(methodPath).forEach(singleMethodPath ->
							resolvedPaths.add(String.format("%s%s", prependSlash(singleClassPath), prependSlash(singleMethodPath)))
						)
					);
					return resolvedPaths.toArray(new String[0]);
				}

				@Override
				public String[] path() {
					String[] classPath = getValue(classMapping, RequestMapping::path, RequestMapping::value, new String[]{""});
					String[] methodPath = getValue(methodMapping, RequestMapping::path, RequestMapping::value, new String[]{""});
					List<String> resolvedPaths = new ArrayList<>();
					Stream.of(classPath).forEach(singleClassPath ->
						Stream.of(methodPath).forEach(singleMethodPath ->
							resolvedPaths.add(String.format("%s%s", prependSlash(singleClassPath), prependSlash(singleMethodPath)))
						)
					);
					return resolvedPaths.toArray(new String[0]);
				}

				@Override
				public RequestMethod[] method() {
					return methodMapping.method();
				}

				@Override
				public String[] params() {
					return methodMapping.params();
				}

				@Override
				public String[] headers() {
					List<String> headers = new ArrayList<>(Arrays.asList(classMapping.headers()));
					for (String methodHeaders : methodMapping.headers()) {
						if (!headers.contains(methodHeaders)) {
							headers.add(methodHeaders);
						}
					}
					return headers.toArray(new String[0]);
				}

				@Override
				public String[] consumes() {
					List<String> consumes = new ArrayList<>(Arrays.asList(classMapping.consumes()));
					for (String methodConsumes : methodMapping.consumes()) {
						if (!consumes.contains(methodConsumes)) {
							consumes.add(methodConsumes);
						}
					}
					return consumes.toArray(new String[0]);
				}

				@Override
				public String[] produces() {
					List<String> produces = new ArrayList<>(Arrays.asList(classMapping.produces()));
					for (String methodProduces : methodMapping.produces()) {
						if (!produces.contains(methodProduces)) {
							produces.add(methodProduces);
						}
					}
					return produces.toArray(new String[0]);
				}

			};
		}
	}

	private String[] getValue(final RequestMapping mapping, final Function<RequestMapping, String[]> primary,
		final Function<RequestMapping, String[]> secondary, final String[] defaultValue) {
		if (primary.apply(mapping).length == 0 && secondary.apply(mapping).length == 0) {
			return defaultValue;
		} else {
			if (!(primary.apply(mapping).length == 0)) {
				return primary.apply(mapping);
			} else {
				return secondary.apply(mapping);
			}
		}
	}

	public String prependSlash(String path) {
		if (path == null || path.trim().isEmpty()) {
			return path;
		} else {
			return path.trim().startsWith("/") ? path : "/" + path;
		}
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
				return new RequestMethod[]{RequestMethod.GET};
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
				return new RequestMethod[]{RequestMethod.POST};
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
				return new RequestMethod[]{RequestMethod.PUT};
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
				return new RequestMethod[]{RequestMethod.DELETE};
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
				return new RequestMethod[]{RequestMethod.PATCH};
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
