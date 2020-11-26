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

import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to merge OpenAPI {@link PathItemObject}.
 *
 * @author chhorz
 */
public class PathItemUtils extends AbstractMergeUtils {

	/**
	 * Creates a new instance of this utility class with the given logger.
	 *
	 * @param logUtils the given logger instance
	 */
	public PathItemUtils(LogUtils logUtils) {
		super(logUtils);
	}

	/**
	 * Merges the given {@link PathItemObject}s and all of their internal components.
	 *
	 * @param pathItemOne the first path item instance
	 * @param pathItemTwo the second path item instance
	 * @return a new instance with data from both path items
	 */
	public PathItemObject mergePathItems(PathItemObject pathItemOne, PathItemObject pathItemTwo) {
		logUtils.logDebug("Merging path item");
		if (pathItemOne == null) {
			return pathItemTwo;
		} else if (pathItemTwo == null) {
			return pathItemOne;
		} else {
			PathItemObject mergedPathItemObject = new PathItemObject();
			mergedPathItemObject.setSummary(mergeDocumentation(pathItemOne.getSummary(), pathItemTwo.getSummary()));
			mergedPathItemObject.setDescription(mergeDocumentation(pathItemOne.getDescription(), pathItemTwo.getDescription()));

			mergedPathItemObject.setGet(mergeOperations(pathItemOne.getGet(), pathItemTwo.getGet()));
			mergedPathItemObject.setPut(mergeOperations(pathItemOne.getPut(), pathItemTwo.getPut()));
			mergedPathItemObject.setPost(mergeOperations(pathItemOne.getPost(), pathItemTwo.getPost()));
			mergedPathItemObject.setDelete(mergeOperations(pathItemOne.getDelete(), pathItemTwo.getDelete()));
			mergedPathItemObject.setOptions(mergeOperations(pathItemOne.getOptions(), pathItemTwo.getOptions()));
			mergedPathItemObject.setHead(mergeOperations(pathItemOne.getHead(), pathItemTwo.getHead()));
			mergedPathItemObject.setPatch(mergeOperations(pathItemOne.getPatch(), pathItemTwo.getPatch()));
			mergedPathItemObject.setTrace(mergeOperations(pathItemOne.getTrace(), pathItemTwo.getTrace()));

			return mergedPathItemObject;
		}
	}

	private Operation mergeOperations(Operation operationOne, Operation operationTwo) {
		if (operationOne == null) {
			return operationTwo;
		} else if (operationTwo == null) {
			return operationOne;
		} else {
			logUtils.logInfo("Merging operation %s and %s", operationOne.getOperationId(), operationTwo.getOperationId());

			Operation mergedOperation = new Operation();
			mergedOperation.setSummary(mergeDocumentation(operationOne.getSummary(), operationTwo.getSummary()));
			mergedOperation.setDescription(mergeDocumentation(operationOne.getDescription(), operationTwo.getDescription()));

			if (operationOne.getTags() != null) {
				operationOne.getTags().stream()
					.filter(tag -> mergedOperation.getTags() == null || !mergedOperation.getTags().contains(tag))
					.forEach(mergedOperation::addTag);
			}
			if (operationTwo.getTags() != null) {
				operationTwo.getTags().stream()
					.filter(tag -> mergedOperation.getTags() == null || !mergedOperation.getTags().contains(tag))
					.forEach(mergedOperation::addTag);
			}

			mergedOperation.setOperationId(operationOne.getOperationId());

			mergedOperation.addParameterObjects(operationOne.getParameterObjects());
			operationTwo.getParameterObjects().stream()
					.filter(parameter -> !mergedOperation.getParameterObjects().contains(parameter))
					.forEach(mergedOperation::addParameterObject);

			if (operationOne.getRequestBodyReference() != null) {
				mergedOperation.setRequestBodyReference(operationOne.getRequestBodyReference());
			} else {
				mergedOperation.setRequestBodyReference(operationTwo.getRequestBodyReference());
			}

			mergedOperation.setResponses(operationOne.getResponses());
			operationTwo.getResponses().forEach((statuscode, response) -> {
				if (mergedOperation.getResponses().containsKey(statuscode)) {
					mergedOperation.putResponse(statuscode, mergeResponses(mergedOperation.getResponses().get(statuscode), response));
				} else {
					mergedOperation.putResponse(statuscode, response);
				}
			});

			mergedOperation.setDeprecated(operationOne.getDeprecated() && operationTwo.getDeprecated());

			List<Map<String, List<String>>> mergedSecurity = new ArrayList<>();
			if (operationOne.getSecurity() != null) {
				mergedSecurity.addAll(operationOne.getSecurity());
			}
			if (operationTwo.getSecurity() != null) {
				operationTwo.getSecurity().stream()
					.filter(security -> !mergedSecurity.contains(security))
					.forEach(mergedSecurity::add);
			}
			mergedOperation.setSecurity(mergedSecurity);

			return mergedOperation;
		}
	}

	private Response mergeResponses(Response responseOne, Response responseTwo) {
		logUtils.logDebug("Merging response");
		if (responseOne == null) {
			return responseTwo;
		} else if (responseTwo == null) {
			return responseOne;
		} else {
			Response mergedResponse = new Response();
			String mergeDocumentation = mergeDocumentation(responseOne.getDescription(), responseTwo.getDescription());
			mergedResponse.setDescription(mergeDocumentation != null ? mergeDocumentation : "");
			if (responseOne.getContent() != null) {
				responseOne.getContent().forEach(mergedResponse::putContent);
			}
			if (responseTwo.getContent() != null) {
				responseTwo.getContent().forEach(mergedResponse::putContent);
			}
			return mergedResponse;
		}
	}

}
