package com.github.chhorz.openapi.common.test.util;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.util.TypeMirrorUtils;

public class TypeMirrorUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private Elements elements;
	private Types types;

	private TypeMirrorUtils typeMirrorUtils;

	@BeforeEach
	void setUpEach() {
		elements = extension.getElements();
		types = extension.getTypes();

		typeMirrorUtils = new TypeMirrorUtils(elements, types);
	}

	@Test
	void test() {
		// given

		// when
		// typeMirrorUtils.removeEnclosingType(originalReturnType, removableClass)

		// then
	}
}
