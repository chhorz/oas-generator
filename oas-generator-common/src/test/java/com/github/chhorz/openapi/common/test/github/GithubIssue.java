package com.github.chhorz.openapi.common.test.github;

import java.lang.annotation.*;

/**
 * Annotation for test cases to document related github issues.
 *
 * @author chhorz
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface GithubIssue {

	String value();

}
