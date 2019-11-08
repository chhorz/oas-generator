package com.github.chhorz.openapi.spi.asciidoctor;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.util.LoggingUtils;
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

	private static final int POST_PROCESSOR_ORDER = 0;

	private AsciidoctorProperties asciidoctorProperties;
	private LoggingUtils log;

	private Configuration freemarkerConfiguration;

	public AsciidoctorPostProcessor(final ParserProperties parserProperties) {
		this.asciidoctorProperties = parserProperties.getPostProcessor("asciidoctor", AsciidoctorProperties.class)
			.orElse(new AsciidoctorProperties());

		log = new LoggingUtils(parserProperties, "[Asciidoctor]");

		final boolean logTemplateExceptions = asciidoctorProperties.getExceptionLogging();
		final boolean localizedLookup = asciidoctorProperties.getLocalizedLookup();

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

		final String templatePath = asciidoctorProperties.getTemplatePath();
		final String templateFile = asciidoctorProperties.getTemplateFile();
		final String outputPath = asciidoctorProperties.getOutputPath();
		final String outputFile = asciidoctorProperties.getOutputFile();

		try {
			Template template = freemarkerConfiguration.getTemplate(String.format("%s/%s", templatePath, templateFile));

			Path outputFilePath = Paths.get(outputPath, outputFile);
			File asciidoctorfile = outputFilePath.toFile();
			log.debug("AsciidoctorPostProcessor | Filepath: " + asciidoctorfile.getAbsolutePath());

			if (!Files.exists(outputFilePath)){
				try {
					Files.createDirectories(outputFilePath.getParent());
					Files.createFile(outputFilePath);

					asciidoctorfile = outputFilePath.toFile();
				} catch (IOException e) {
					log.error("Could not create output file", e);
				}
			}

			FileWriter fileWriter = new FileWriter(asciidoctorfile);

			template.process(prepareTemplateVariables(openApi), fileWriter);
		} catch (IOException e) {
			log.error(String.format("Could not load template=%s", templateFile), e);
		} catch (TemplateException e) {
			log.error(String.format("Error while templating %s", templateFile), e);
		}

		log.info("AsciidoctorPostProcessor | Finish");
	}

	private Map<String, Object> prepareTemplateVariables(final OpenAPI openAPI){
		Map<String, Object> templateVariables = new HashMap<>();
		templateVariables.put("openapi", openAPI);
		templateVariables.put("standalone", asciidoctorProperties.getStandaloneFile());
		return templateVariables;
	}

	@Override
	public int getPostProcessorOrder() {
		return POST_PROCESSOR_ORDER;
	}

}
