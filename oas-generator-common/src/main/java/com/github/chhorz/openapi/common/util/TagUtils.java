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

import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.domain.Tag;
import com.github.chhorz.openapi.common.exception.SpecificationViolationException;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Utility class for OpenAPI {@link Tag}s.
 *
 * @author chhorz
 */
public class TagUtils {

	private final GeneratorPropertyLoader propertyLoader;

	public TagUtils(final GeneratorPropertyLoader propertyLoader) {
		this.propertyLoader = propertyLoader;
	}

	/**
	 * Method to aggregate all tags from a given {@link PathItemObject}.
	 *
	 * @param pathItemObject the requested path item
	 * @return a list of tags
	 */
	public List<String> getAllTags(final PathItemObject pathItemObject) {
		List<String> result = new ArrayList<>();

		if (pathItemObject != null) {
			result.addAll(getTags(pathItemObject, PathItemObject::getDelete));
			result.addAll(getTags(pathItemObject, PathItemObject::getGet));
			result.addAll(getTags(pathItemObject, PathItemObject::getHead));
			result.addAll(getTags(pathItemObject, PathItemObject::getOptions));
			result.addAll(getTags(pathItemObject, PathItemObject::getPatch));
			result.addAll(getTags(pathItemObject, PathItemObject::getPost));
			result.addAll(getTags(pathItemObject, PathItemObject::getPut));
			result.addAll(getTags(pathItemObject, PathItemObject::getTrace));
		}

		return result;
	}

	private List<String> getTags(final PathItemObject pathItemObject, final Function<PathItemObject, Operation> function) {
		Operation operation = function.apply(pathItemObject);
		if (operation != null && operation.getTags() != null) {
			return operation.getTags().stream().filter(tag -> tag != null && !tag.isEmpty()).collect(toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Create a tag with a given name. Additional information like description and external docs
	 * are added from the properties.
	 *
	 * @param tagName the name of the new tag
	 * @return the OpenAPI {@link Tag}
	 */
	public Tag createTag(final String tagName) {
		if (tagName == null) {
			throw new SpecificationViolationException("Missing property 'name' for 'Tag' object");
		}

		Tag tag = new Tag();
		tag.setName(tagName);
		tag.setDescription(propertyLoader.getDescriptionForTag(tagName));
		tag.setExternalDocs(propertyLoader.getExternalDocumentationForTag(tagName));
		return tag;
	}

}
