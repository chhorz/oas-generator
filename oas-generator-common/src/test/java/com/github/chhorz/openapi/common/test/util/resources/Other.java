package com.github.chhorz.openapi.common.test.util.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class Other {

	@JsonProperty("int")
	public int i;

	public LocalDate date;
	@Deprecated
	public LocalDateTime time;

	@JsonIgnore
	public TestEnum enumeration;

}
