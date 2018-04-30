package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.TestClass;
import com.github.chhorz.openapi.common.util.TypeMirrorUtils;

public class TypeMirrorUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private static Elements elements;
	private static Types types;

	private TypeMirrorUtils typeMirrorUtils;

	@BeforeEach
	void setUpEach() {
		elements = extension.getElements();
		types = extension.getTypes();

		typeMirrorUtils = new TypeMirrorUtils(elements, types);
	}

	@Test
	void testList() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();
		TypeMirror typedList = types.getDeclaredType(elements.getTypeElement(List.class.getCanonicalName()), doubleType);

		// when
		TypeMirror type = typeMirrorUtils.removeEnclosingType(typedList, List.class);

		// then
		assertThat(type).isEqualTo(doubleType);
	}

	@Test
	void testSet() {
		// given
		TypeMirror testType = elements.getTypeElement(TestClass.class.getCanonicalName()).asType();
		TypeMirror typedList = types.getDeclaredType(elements.getTypeElement(Set.class.getCanonicalName()), testType);

		// when
		TypeMirror type = typeMirrorUtils.removeEnclosingType(typedList, Set.class);

		// then
		assertThat(type).isEqualTo(testType);
	}
}
