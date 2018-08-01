package com.github.chhorz.openapi.common.test.javadoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;

public class ResponseTagTest {

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
