package com.github.chhorz.openapi.common.util;

import com.github.chhorz.openapi.common.SpecificationViolationException;
import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.domain.Tag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class TagUtils {

	private GeneratorPropertyLoader propertyLoader;

	public TagUtils(final GeneratorPropertyLoader propertyLoader) {
		this.propertyLoader = propertyLoader;
	}

	public List<String> getAllTags(final PathItemObject pathItemObject) {
		List<String> result = new ArrayList<>();

		if (pathItemObject != null) {
			getTags(pathItemObject, PathItemObject::getDelete).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getGet).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getHead).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getOptions).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getPatch).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getPost).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getPut).forEach(result::add);
			getTags(pathItemObject, PathItemObject::getTrace).forEach(result::add);
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
