package com.github.chhorz.openapi.spring.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.spring.SpringWebOpenApiProcessor;

/**
 * http://jcavallotti.blogspot.de/2013/05/how-to-unit-test-annotation-processor.html
 *
 * @author chhorz
 *
 */
public class SpringWebOpenApiProcessorTest {

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

	@Test
	void aTest() {
		// the files to be compiled.
		String[] files = new String[] {
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/OrderController.java",
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/resource/BaseResource.java",
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/resource/Order.java",
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/resource/Article.java",
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/resource/PrimitiveResource.java" };
		try {
			// streams.
			ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
			OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

			JavaCompiler.CompilationTask task = javaCompiler.getTask(stdout, fileManager, collector, null, null,
					fileManager.getJavaFileObjects(files));
			task.setProcessors(Arrays.asList(new SpringWebOpenApiProcessor()));
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

	@Test
	@Disabled
	void bTest() {
		// the files to be compiled.
		String[] files = new String[] {
				"src/test/java/com/github/chhorz/openapi/spring/test/controller/ArticleController.java" };
		try {
			// streams.
			ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
			OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

			JavaCompiler.CompilationTask task = javaCompiler.getTask(stdout, fileManager, collector,
					Arrays.asList(
							"-ApropertiesPath=/Users/chorz/eclipse-workspace/openapi-spec-generator/spring-web-spec-generator/src/test/resources/openapi.properties"),
					null,
					fileManager.getJavaFileObjects(files));
			task.setProcessors(Arrays.asList(new SpringWebOpenApiProcessor()));
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
			System.out.println("Finally");
		}
	}

}
