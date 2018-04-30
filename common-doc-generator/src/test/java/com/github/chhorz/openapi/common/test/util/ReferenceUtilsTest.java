package com.github.chhorz.openapi.common.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.TestClass;
import com.github.chhorz.openapi.common.util.ReferenceUtils;

public class ReferenceUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private Elements elements;

	@BeforeEach
	void setUpEach() {
		elements = extension.getElements();
	}

	@Test
	void nullSchema() {
		assertThatThrownBy(() -> ReferenceUtils.createSchemaReference(null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void validSchema() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();

		// when
		Reference reference = ReferenceUtils.createSchemaReference(doubleType);

		// then
		assertThat(reference)
				.hasFieldOrPropertyWithValue("$ref", "#/components/schemas/Double");
	}

	@Test
	void nullRequestBody() {
		assertThatThrownBy(() -> ReferenceUtils.createRequestBodyReference(null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void validRequestBody() {
		// given
		TypeMirror testClassMirror = elements.getTypeElement(TestClass.class.getCanonicalName()).asType();

		// when
		Reference reference = ReferenceUtils.createRequestBodyReference(testClassMirror);

		// then
		assertThat(reference)
				.hasFieldOrPropertyWithValue("$ref", "#/components/requestBodies/TestClass");
	}
}
