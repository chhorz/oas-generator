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
package com.github.chhorz.openapi.spring.util;

import com.github.chhorz.openapi.common.util.LogUtils;

import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * Abstract class for merging OpenAPI domain objects.
 *
 * @author chhorz
 */
public abstract class AbstractMergeUtils {

	/**
	 * Predicate to check if a String is not null and has content
	 */
	private static final Predicate<String> PRESENCE = s -> s != null && !s.isEmpty();

	protected final LogUtils logUtils;

	public AbstractMergeUtils(LogUtils logUtils) {
		this.logUtils = logUtils;
	}

	/**
	 * Merges two description strings
	 *
	 * @param descriptionOne description one
	 * @param descriptionTwo description two
	 * @return the combined description String.
	 */
	protected String mergeDescription(String descriptionOne, String descriptionTwo) {
		if (PRESENCE.test(descriptionOne) && PRESENCE.test(descriptionTwo) && descriptionOne.equalsIgnoreCase(descriptionTwo)) {
			return descriptionOne;
		} else if (PRESENCE.test(descriptionOne) && PRESENCE.test(descriptionTwo)) {
			return format("%s\n<hr></hr>\n%s", descriptionOne, descriptionTwo).trim();
		} else if (PRESENCE.test(descriptionOne) && !PRESENCE.test(descriptionTwo)) {
			return descriptionOne;
		} else if (!PRESENCE.test(descriptionOne) && PRESENCE.test(descriptionTwo)) {
			return descriptionTwo;
		} else {
			return null;
		}
	}

	/**
	 * Merges two summary strings
	 *
	 * @param summaryOne summary one
	 * @param summaryTwo summary two
	 * @return the combined summary String.
	 */
	protected String mergeSummary(String summaryOne, String summaryTwo) {
		if (PRESENCE.test(summaryOne) && PRESENCE.test(summaryTwo) && summaryOne.equalsIgnoreCase(summaryTwo)) {
			return summaryOne;
		} else if (PRESENCE.test(summaryOne) && PRESENCE.test(summaryTwo)) {
			return "Merged data - please see description.";
		} else if (PRESENCE.test(summaryOne) && !PRESENCE.test(summaryTwo)) {
			return summaryOne;
		} else if (!PRESENCE.test(summaryOne) && PRESENCE.test(summaryTwo)) {
			return summaryTwo;
		} else {
			return null;
		}
	}

}
