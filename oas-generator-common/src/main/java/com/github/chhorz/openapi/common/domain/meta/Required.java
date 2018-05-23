package com.github.chhorz.openapi.common.domain.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated attribute must be present in the resulting OpenAPI document.
 *
 * @author chhorz
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Required {

}
