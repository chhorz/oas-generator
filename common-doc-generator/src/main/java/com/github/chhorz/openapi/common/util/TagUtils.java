package com.github.chhorz.openapi.common.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.PathItemObject;

public class TagUtils {

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

}
