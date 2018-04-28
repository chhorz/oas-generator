package com.github.chhorz.openapi.common.test.extension;

import static java.util.stream.Collectors.toSet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ProcessingUtilsExtension implements BeforeEachCallback {

	private Elements elements;
	private Types types;

	@Override
	public void beforeEach(final ExtensionContext context) throws Exception {
		javac();
	}

	/**
	 * Returns the {@link Elements} instance associated with the current execution of the rule.
	 *
	 * @throws IllegalStateException
	 *             if this method is invoked outside the execution of the rule.
	 */
	public Elements getElements() {
		if (elements == null) {
			throw new IllegalStateException("elements is null!");
		}
		return elements;
	}

	/**
	 * Returns the {@link Types} instance associated with the current execution of the rule.
	 *
	 * @throws IllegalStateException
	 *             if this method is invoked outside the execution of the rule.
	 */
	public Types getTypes() {
		if (types == null) {
			throw new IllegalStateException("types is null!");
		}
		return types;
	}

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
		}

		@Override
		public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
			// just run the test on the last round after compilation is over
			if (roundEnv.processingOver()) {
				// base.evaluate();
			}
			return false;
		}
	}

	public void javac() {
		JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		StandardJavaFileManager fileManager = systemJavaCompiler.getStandardFileManager(collector, Locale.US,
				Charset.forName("UTF-8"));

		ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
		OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

		String[] files = new String[] { "src/test/java/com/github/chhorz/openapi/common/test/extension/Dummy.java",
				"src/test/java/com/github/chhorz/openapi/common/test/util/resources/Test.java" };

		JavaCompiler.CompilationTask compilationTask = systemJavaCompiler.getTask(stdout, fileManager, collector, null, null,
				fileManager.getJavaFileObjects(files));
		compilationTask.setProcessors(Arrays.asList(new EvaluatingProcessor()));

		compilationTask.call();
	}

}
