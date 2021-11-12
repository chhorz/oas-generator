package com.github.chhorz.openapi.spring.test.github.resources;

import java.util.List;
import java.util.Map;

/**
 * a test resource for GitHub issue tests
 */
public class TestResource {

	public List<State> states;

	public Map<String, String> stringMap;

	public enum State {
		GOOD,BAD
	}

}
