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

import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.test.extension.ProcessingUtilsExtension;
import com.github.chhorz.openapi.common.test.util.resources.ClassC;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.ProcessingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledForJreRange(max = JRE.JAVA_8)
public class ProcessingUtilsTest {

	@RegisterExtension
	ProcessingUtilsExtension extension = new ProcessingUtilsExtension();

	private static Elements elements;
	private static Types types;

	private ProcessingUtils processingUtils;

	@BeforeEach
	void setUpEach() {
		ParserProperties parserProperties = new ParserProperties();
		parserProperties.setLogLevel("DEBUG");

		elements = extension.getElements();
		types = extension.getTypes();

		processingUtils = new ProcessingUtils(elements, types, new LogUtils(null, parserProperties));
	}

	@Test
	void testList() {
		// given
		TypeMirror doubleType = elements.getTypeElement(Double.class.getCanonicalName()).asType();
		TypeMirror typedList = types.getDeclaredType(elements.getTypeElement(List.class.getCanonicalName()), doubleType);

		// when
		TypeMirror[] typeArray = processingUtils.removeEnclosingType(typedList, List.class);

		// then
		assertThat(typeArray)
			.hasSize(1)
			.contains(doubleType);
	}

	@Test
	void testSet() {
		// given
		TypeMirror testType = elements.getTypeElement(ClassC.class.getCanonicalName()).asType();
		TypeMirror typedList = types.getDeclaredType(elements.getTypeElement(Set.class.getCanonicalName()), testType);

		// when
		TypeMirror[] typeArray = processingUtils.removeEnclosingType(typedList, Set.class);

		// then
		assertThat(typeArray)
			.hasSize(1)
			.contains(testType);
	}

	@Test
	void testCreateArrayFromString(){
		// given
		String input = "java.lang.String[]";

		// when
		TypeMirror typeMirror = processingUtils.createTypeMirrorFromString(input);

		// then
		assertThat(typeMirror)
				.isNotNull();

		assertThat(typeMirror.getKind())
				.isEqualTo(TypeKind.ARRAY);
	}
}
