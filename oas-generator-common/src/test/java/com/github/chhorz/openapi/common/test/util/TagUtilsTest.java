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
