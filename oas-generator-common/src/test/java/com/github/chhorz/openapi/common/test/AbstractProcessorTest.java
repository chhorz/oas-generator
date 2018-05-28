package com.github.chhorz.openapi.common.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractProcessorTest {

	private static JavaCompiler javaCompiler;

	private StandardJavaFileManager fileManager;
	private DiagnosticCollector<JavaFileObject> collector;

	@BeforeAll
	static void initialize() {
		javaCompiler = ToolProvider.getSystemJavaCompiler();
	}

	@BeforeEach
	void initTest() throws Exception {
		// configure the diagnostics collector.
		collector = new DiagnosticCollector<>();
		fileManager = javaCompiler.getStandardFileManager(collector, Locale.US, Charset.forName("UTF-8"));
	}

	public void testCompilation(final Processor processor, final Class<?>... classes) {
		testCompilation(processor,
				Stream.of(classes)
						.map(clazz -> clazz.getCanonicalName())
						.map(clazz -> clazz.replaceAll("\\.", "/"))
						.map(clazz -> String.format("src/test/java/%s.java", clazz))
						.toArray(String[]::new));
	}

	private void testCompilation(final Processor processor, final String... files) {
		System.out.println(Arrays.toString(files));
		try {
			// streams.
			ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
			OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

			JavaCompiler.CompilationTask task = javaCompiler.getTask(stdout, fileManager, collector, null, null,
					fileManager.getJavaFileObjects(files));
			task.setProcessors(Arrays.asList(processor));
			Boolean result = task.call();

			String stdoutS = new String(stdoutStream.toByteArray());

			// perform the verifications.
			for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
				if (diagnostic.getKind() == Kind.MANDATORY_WARNING || diagnostic.getKind() == Kind.ERROR) {
					fail("Failed with message: " + diagnostic.getMessage(null));
				}
			}

			assertThat(result).isTrue().as("Files should have no compilation errors");
		} finally {
			// no-op
		}
	}

}
