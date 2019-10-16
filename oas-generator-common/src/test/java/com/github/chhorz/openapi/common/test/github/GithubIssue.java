package com.github.chhorz.openapi.common.test.github;

import com.github.chhorz.openapi.common.annotation.OpenAPISchema;

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

	int number();

}
