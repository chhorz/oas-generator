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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class PathItemUtils {

	public PathItemObject mergePathItems(PathItemObject pathItemOne, PathItemObject pathItemTwo) {
		if (pathItemOne == null) {
			return pathItemTwo;
		} else if (pathItemTwo == null) {
			return pathItemOne;
		} else {
			PathItemObject mergedPathItemObject = new PathItemObject();
			mergedPathItemObject.setSummary(format("%s\n\n%s", pathItemOne.getSummary(), pathItemTwo.getSummary()));
			mergedPathItemObject.setDescription(format("%s\n\n%s", pathItemOne.getDescription(), pathItemTwo.getDescription()));

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

	public Operation mergeOperations(Operation operationOne, Operation operationTwo) {
		if (operationOne == null) {
			return operationTwo;
		} else if (operationTwo == null) {
			return operationOne;
		} else {
			Operation mergedOperation = new Operation();
			mergedOperation.setSummary(format("%s\n\n%s", operationOne.getSummary(), operationTwo.getSummary()));
			mergedOperation.setDescription(format("%s\n\n%s", operationOne.getDescription(), operationTwo.getDescription()));

			if (operationOne.getTags() != null) {
				operationOne.getTags().forEach(mergedOperation::addTag);
			}
			if (operationTwo.getTags() != null) {
				operationTwo.getTags().forEach(mergedOperation::addTag);
			}

			mergedOperation.setOperationId(operationOne.getOperationId());

			mergedOperation.addParameterObjects(operationOne.getParameterObjects());
			mergedOperation.addParameterObjects(operationTwo.getParameterObjects());

			if (operationOne.getRequestBodyReference() != null) {
				mergedOperation.setRequestBodyReference(operationOne.getRequestBodyReference());
			} else {
				mergedOperation.setRequestBodyReference(operationTwo.getRequestBodyReference());
			}

			mergedOperation.setResponses(operationOne.getResponses());
			operationTwo.getResponses().forEach(mergedOperation::putResponse);

			mergedOperation.setDeprecated(operationOne.getDeprecated() && operationTwo.getDeprecated());

			List<Map<String, List<String>>> mergedSecurity = new ArrayList<>();
			if (operationOne.getSecurity() != null) {
				mergedSecurity.addAll(operationOne.getSecurity());
			}
			if (operationTwo.getSecurity() != null) {
				mergedSecurity.addAll(operationTwo.getSecurity());
			}
			mergedOperation.setSecurity(mergedSecurity);

			return mergedOperation;
		}
	}

}
