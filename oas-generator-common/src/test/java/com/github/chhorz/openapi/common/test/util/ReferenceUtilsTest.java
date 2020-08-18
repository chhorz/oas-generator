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

import com.github.chhorz.openapi.common.domain.Reference;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.ClassC;
import com.github.chhorz.openapi.common.util.ReferenceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnabledForJreRange(max = JRE.JAVA_8)
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
		TypeMirror classCType = elements.getTypeElement(ClassC.class.getCanonicalName()).asType();

		// when
		Reference reference = ReferenceUtils.createRequestBodyReference(classCType);

		// then
		assertThat(reference)
				.hasFieldOrPropertyWithValue("$ref", "#/components/requestBodies/ClassC");
	}
}
