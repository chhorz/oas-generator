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
package com.github.chhorz.openapi.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.domain.meta.Markdown;

import java.util.List;

/**
 * https://spec.openapis.org/oas/v3.1.0#path-item-object
 *
 * @author chhorz
 *
 */
public class PathItemObject {

	private String $ref;
	private String summary;
	@Markdown
	private String description;
	private Operation get;
	private Operation put;
	private Operation post;
	private Operation delete;
	private Operation options;
	private Operation head;
	private Operation patch;
	private Operation trace;
	private List<Server> servers;
	@JsonProperty("parameters")
	private List<Parameter> parameterObjects;
	// @JsonProperty("parameters")
	// private List<Reference> parameterReferences;


	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Operation getGet() {
		return get;
	}

	public void setGet(final Operation get) {
		this.get = get;
	}

	public Operation getPut() {
		return put;
	}

	public void setPut(final Operation put) {
		this.put = put;
	}

	public Operation getPost() {
		return post;
	}

	public void setPost(final Operation post) {
		this.post = post;
	}

	public Operation getDelete() {
		return delete;
	}

	public void setDelete(final Operation delete) {
		this.delete = delete;
	}

	public Operation getOptions() {
		return options;
	}

	public void setOptions(final Operation options) {
		this.options = options;
	}

	public Operation getHead() {
		return head;
	}

	public void setHead(final Operation head) {
		this.head = head;
	}

	public Operation getPatch() {
		return patch;
	}

	public void setPatch(final Operation patch) {
		this.patch = patch;
	}

	public Operation getTrace() {
		return trace;
	}

	public void setTrace(final Operation trace) {
		this.trace = trace;
	}

}
