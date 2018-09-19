package com.github.chhorz.openapi.spi.asciidoctor;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.util.LoggingUtils;
import freemarker.core.OutputFormat;
import freemarker.core.PlainTextOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AsciidoctorPostProcessor implements OpenAPIPostProcessor {

	private static final String DEFAULT_TEMPLATE_FOLDER = "/templates/freemarker";
	private static final String DEFAULT_TEMPLATE_NAME = "openapi.ftlh";
	private static final boolean DEFAULT_EXCEPTION_LOGGING = true;
	private static final String DEFAULT_OUTPUT_DIR = "/openapi";
	private static final String DEFAULT_OUTPUT_FILE = "/openapi.adoc";
	private static final boolean DEFAULT_LOCALIZED_LOOKUP = false;

	private ParserProperties parserProperties;
	private LoggingUtils log;

	private Configuration freemarkerConfiguration;

	public AsciidoctorPostProcessor(final ParserProperties parserProperties) {
		this.parserProperties = parserProperties;
		log = new LoggingUtils(parserProperties, "[Asciidoctor]");

		final boolean logTemplateExceptions = parserProperties.getPostProcessorValue("asciidoctor.logging", DEFAULT_EXCEPTION_LOGGING);
		final boolean localizedLookup = parserProperties.getPostProcessorValue("asciidoctor.template.localizedLookup", DEFAULT_LOCALIZED_LOOKUP);

		freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);
		freemarkerConfiguration.setOutputFormat(PlainTextOutputFormat.INSTANCE);
		freemarkerConfiguration.setLocalizedLookup(localizedLookup);
		freemarkerConfiguration.setDefaultEncoding("UTF-8");
		freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		freemarkerConfiguration.setLogTemplateExceptions(logTemplateExceptions);
		freemarkerConfiguration.setClassForTemplateLoading(AsciidoctorPostProcessor.class, "/");
	}

	@Override
	public void execute(final OpenAPI openApi) {
		log.info("AsciidoctorPostProcessor | Start");

		final String templateFolder = parserProperties.getPostProcessorValue("asciidoctor.template.folder", DEFAULT_TEMPLATE_FOLDER);
		final String templateName = parserProperties.getPostProcessorValue("asciidoctor.template.name", DEFAULT_TEMPLATE_NAME);
		final String outputDir = parserProperties.getPostProcessorValue("asciidoctor.output.dir", DEFAULT_OUTPUT_DIR);
		final String outputFile = parserProperties.getPostProcessorValue("asciidoctor.output.file", DEFAULT_OUTPUT_FILE);

		try {
			Template template = freemarkerConfiguration.getTemplate(String.format("%s/%s", templateFolder, templateName));


			Path outputPath = Paths.get(outputDir, outputFile);
			File asciidoctorfile = outputPath.toFile();
			log.debug("AsciidoctorPostProcessor | Filepath: " + asciidoctorfile.getAbsolutePath());

			if (!Files.exists(outputPath)){
				try {
					Files.createDirectories(outputPath.getParent());
					Files.createFile(outputPath);

					asciidoctorfile = outputPath.toFile();
				} catch (IOException e) {
					log.error("Could not create output file", e);
				}
			}
			FileWriter fileWriter = new FileWriter(asciidoctorfile);

			template.process(prepareTemplateVariables(openApi), fileWriter);
		} catch (IOException e) {
			log.error(String.format("Could not load template=%s", templateName), e);
		} catch (TemplateException e) {
			log.error(String.format("Error while templating %s", templateName), e);
		}

		log.info("AsciidoctorPostProcessor | Finish");
	}

	private Map<String, Object> prepareTemplateVariables(final OpenAPI openAPI){
		Map<String, Object> templateVariables = new HashMap<>();
		templateVariables.put("openapi", openAPI);
		return templateVariables;
	}

}
