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
package com.github.chhorz.openapi.spi.asciidoctor;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorType;
import com.github.chhorz.openapi.common.util.LogUtils;
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
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * The Asciidoctor post processor that used the Freemarker template engine to
 * convert the internal OpenAPI domain object into an Asciidoctor file.
 *
 * @author chhorz
 */
public class AsciidoctorPostProcessor implements OpenAPIPostProcessor {

	private static final int POST_PROCESSOR_ORDER = 0;

	private final AsciidoctorProperties asciidoctorProperties;
	private final LogUtils logUtils;

	private final Configuration freemarkerConfiguration;

	/**
	 * Instantiation of the Asciidoctor post processor and configuration of
	 * the Freemarker template engine.
	 *
	 * @param logUtils the oas-generator internal logging utils class
	 * @param parserProperties the complete parser properties
	 */
	public AsciidoctorPostProcessor(final LogUtils logUtils, final ParserProperties parserProperties) {
		this.asciidoctorProperties = parserProperties.getPostProcessor("asciidoctor", AsciidoctorProperties.class)
			.orElse(new AsciidoctorProperties());

		this.logUtils = logUtils.configureWithComponent("[Asciidoctor]");

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final OpenAPI openApi) {
		logUtils.logInfo("AsciidoctorPostProcessor | Start");

		final String templatePath = asciidoctorProperties.getTemplatePath();
		final String templateFile = asciidoctorProperties.getTemplateFile();
		final String outputPath = asciidoctorProperties.getOutputPath();
		final String outputFile = asciidoctorProperties.getOutputFile();

		try {
			Template template = freemarkerConfiguration.getTemplate(String.format("%s/%s", templatePath, templateFile));

			Path outputFilePath = Paths.get(outputPath, outputFile);
			File asciidoctorfile = outputFilePath.toFile();
			logUtils.logDebug("AsciidoctorPostProcessor | Filepath: " + asciidoctorfile.getAbsolutePath());

			if (!Files.exists(outputFilePath)){
				try {
					Files.createDirectories(outputFilePath.getParent());
					Files.createFile(outputFilePath);

					asciidoctorfile = outputFilePath.toFile();
				} catch (IOException e) {
					logUtils.logError("Could not create output file", e);
				}
			}

			FileWriter fileWriter = new FileWriter(asciidoctorfile);

			template.process(prepareTemplateVariables(openApi), fileWriter);
		} catch (IOException e) {
			logUtils.logError(String.format("Could not load template=%s", templateFile), e);
		} catch (TemplateException e) {
			logUtils.logError(String.format("Error while templating %s", templateFile), e);
		}

		logUtils.logInfo("AsciidoctorPostProcessor | Finish");
	}

	private Map<String, Object> prepareTemplateVariables(final OpenAPI openAPI){
		Map<String, Object> templateVariables = new HashMap<>();
		templateVariables.put("openapi", openAPI);
		templateVariables.put("standalone", asciidoctorProperties.getStandaloneFile());
		return templateVariables;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPostProcessorOrder() {
		return POST_PROCESSOR_ORDER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PostProcessorType> getPostProcessorType() {
		return singletonList(PostProcessorType.DOMAIN_OBJECT);
	}
}
