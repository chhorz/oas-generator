package com.github.chhorz.openapi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate specific java classes as a OpenAPI schema. This annotation should be used if
 * the resources are not registered automatically as OpenAPI schema. It must be used if the classes for the REST
 * resources are placed in a separate maven module for example.
 *
 * @author chhorz
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface OpenAPISchema {

}
