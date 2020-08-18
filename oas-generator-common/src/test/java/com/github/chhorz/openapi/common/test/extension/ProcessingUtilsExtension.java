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
package com.github.chhorz.openapi.common.test.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

public class ProcessingUtilsExtension implements BeforeEachCallback {

	private Elements elements;
	private Types types;
	private Messager messager;

	@Override
	public void beforeEach(final ExtensionContext context) {
		javac();
	}

	/**
	 * Returns the {@link Elements} instance.
	 *
	 * @throws IllegalStateException
	 *             if the value is null.
	 */
	public Elements getElements() {
		if (elements == null) {
			throw new IllegalStateException("'elements' is null!");
		}
		return elements;
	}

	/**
	 * Returns the {@link Types} instance.
	 *
	 * @throws IllegalStateException
	 *             if the value is null.
	 */
	public Types getTypes() {
		if (types == null) {
			throw new IllegalStateException("'types' is null!");
		}
		return types;
	}

	/**
	 * Returns the {@link Messager} instance.
	 *
	 * @throws IllegalStateException
	 *             if the value is null.
	 */
	public Messager getMessager() {
		if (messager == null) {
			throw new IllegalStateException("'messager' is null!");
		}
		return messager;
	}

	private void javac() {
		JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		StandardJavaFileManager fileManager = systemJavaCompiler.getStandardFileManager(collector, Locale.US, StandardCharsets.UTF_8);

		ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
		OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

		String[] files = new String[]{
			"src/test/java/com/github/chhorz/openapi/common/test/extension/Dummy.java",
			"src/test/java/com/github/chhorz/openapi/common/test/util/resources/ClassC.java"
		};

		JavaCompiler.CompilationTask compilationTask = systemJavaCompiler.getTask(stdout, fileManager, collector, null, null,
			fileManager.getJavaFileObjects(files));
		compilationTask.setProcessors(singletonList(new EvaluatingProcessor()));

		compilationTask.call();
	}

	/**
	 * Custom annotation processor that is used to fill the fields within the JUnit extension.
	 */
	final class EvaluatingProcessor extends AbstractProcessor {

		@Override
		public SourceVersion getSupportedSourceVersion() {
			return SourceVersion.latest();
		}

		@Override
		public Set<String> getSupportedAnnotationTypes() {
			return Stream.of("*").collect(toSet());
		}

		@Override
		public synchronized void init(final ProcessingEnvironment processingEnv) {
			super.init(processingEnv);

			elements = processingEnv.getElementUtils();
			types = processingEnv.getTypeUtils();
			messager = processingEnv.getMessager();
		}

		@Override
		public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
			return false;
		}
	}

}
