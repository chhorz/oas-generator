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
package com.github.chhorz.openapi.common.test.javadoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;

class ResponseTagTest {

	// @formatter:off
	private String javaDocString =
			"Test\n" +
			"@since 0.1\n" +
			"@author name\n" +
			"@response 201 String \n" + // The resulting number.
			"@deprecated use xyz instead\n" +
			"@version 1.2.3\n" +
			"@see documentation in section abc\n" +
			"@throws NullpointerException when something is null\n" +
			"@exception RuntimeException may occur always\n" +
			"@param test value\n" +
			"@return the result";
	// @formatter:on

	private JavaDocParser parser;

	@BeforeEach
	void setUp() {
		parser = JavaDocParserBuilder.withBasicTags().withCustomTag(new ResponseTag()).build();
	}

	@Test
	void parseResponseTag() {
		JavaDoc javaDoc = parser.parse(javaDocString);

		assertThat(javaDoc.getTags(ResponseTag.class))
			.isNotNull()
			.isNotEmpty()
			.hasSize(1)
				.extracting(ResponseTag::getStatusCode, ResponseTag::getResponseType/* , ResponseTag::getDescription */)
				.contains(tuple("201", "String"/* , "The resulting number." */));

	}
}
