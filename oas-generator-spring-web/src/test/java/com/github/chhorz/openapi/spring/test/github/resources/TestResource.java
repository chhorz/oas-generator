package com.github.chhorz.openapi.spring.test.github.resources;

import java.util.List;

/**
 * a test resource for GitHub issue tests
 */
public class TestResource {

	public List<State> states;

	public enum State {
		GOOD,BAD
	}

}
