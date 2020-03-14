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
package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.util.TagUtils;

public class TagUtilsTest {

	private TagUtils utils;

	@BeforeEach
	void setUp() {
		utils = new TagUtils(null);
	}

	@Test
	void testNull() {
		// given
		PathItemObject pathItemObject = null;

		// when
		List<String> tags = utils.getAllTags(pathItemObject);

		// then
		assertThat(tags).isNotNull().isEmpty();
	}

	@Test
	void testNew() {
		// given
		PathItemObject pathItemObject = new PathItemObject();

		// when
		List<String> tags = utils.getAllTags(pathItemObject);

		// then
		assertThat(tags).isNotNull().isEmpty();
	}

	@Test
	void testPathItemObject() {
		// given
		PathItemObject pathItemObject = new PathItemObject();
		pathItemObject.setDelete(new Operation());
		Operation operation1 = new Operation();
		operation1.addTag(null);
		pathItemObject.setGet(operation1);
		Operation operation2 = new Operation();
		operation2.addTag("");
		pathItemObject.setHead(operation2);
		Operation operation3 = new Operation();
		operation3.addTag("tag1");
		pathItemObject.setOptions(operation3);
		Operation operation4 = new Operation();
		operation4.addTag("tag2");
		operation4.addTag("tag3");
		pathItemObject.setPatch(operation4);

		// when
		List<String> tags = utils.getAllTags(pathItemObject);

		// then
		assertThat(tags).isNotNull().containsExactly("tag1", "tag2", "tag3");
	}

	// TODO test new method
}
