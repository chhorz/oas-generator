package com.github.chhorz.openapi.common.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * http://spec.openapis.org/oas/v3.0.3#path-item-object
 *
 * @author chhorz
 *
 */
public class PathItemObject {

	private String $ref;
	private String summary;
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
