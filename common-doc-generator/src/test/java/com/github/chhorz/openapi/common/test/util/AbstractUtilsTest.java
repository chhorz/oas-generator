package com.github.chhorz.openapi.common.test.util;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class AbstractUtilsTest {

	private Elements elements;
	private Types types;

	public AbstractUtilsTest(final Elements elements, final Types types) {
		this.elements = elements;
		this.types = types;
	}

}
