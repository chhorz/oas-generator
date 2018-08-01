package com.github.chhorz.openapi.common.util;

import java.util.ArrayList;
import java.util.List;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.tags.AuthorTag;
import com.github.chhorz.javadoc.tags.CategoryTag;
import com.github.chhorz.javadoc.tags.DeprecatedTag;
import com.github.chhorz.javadoc.tags.ExceptionTag;
import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.javadoc.tags.ReturnTag;
import com.github.chhorz.javadoc.tags.SeeTag;
import com.github.chhorz.javadoc.tags.SinceTag;
import com.github.chhorz.javadoc.tags.StructuredTag;
import com.github.chhorz.javadoc.tags.ThrowsTag;
import com.github.chhorz.javadoc.tags.VersionTag;

public class JavadocUtils {

	public static JavaDoc mergeJavaDoc(final JavaDoc classJavadoc, final JavaDoc methodJavadoc) {
		JavaDoc mergeResult = new JavaDoc(methodJavadoc.getSummary(), methodJavadoc.getDescription(), new ArrayList<>());

		// TODO add custom defined tags

		// information only from method
		mergeResult.getTags().addAll(methodJavadoc.getTags(CategoryTag.class));
		mergeResult.getTags().addAll(methodJavadoc.getTags(ExceptionTag.class));
		mergeResult.getTags().addAll(methodJavadoc.getTags(ParamTag.class));
		mergeResult.getTags().addAll(methodJavadoc.getTags(ReturnTag.class));
		mergeResult.getTags().addAll(methodJavadoc.getTags(SeeTag.class));
		mergeResult.getTags().addAll(methodJavadoc.getTags(ThrowsTag.class));

		// information from class if not set on method
		mergeResult.getTags().addAll(doMerge(AuthorTag.class, classJavadoc, methodJavadoc));
		mergeResult.getTags().addAll(doMerge(DeprecatedTag.class, classJavadoc, methodJavadoc));
		mergeResult.getTags().addAll(doMerge(SinceTag.class, classJavadoc, methodJavadoc));
		mergeResult.getTags().addAll(doMerge(VersionTag.class, classJavadoc, methodJavadoc));

		return mergeResult;
	}

	private static <T extends StructuredTag> List<T> doMerge(final Class<T> clazz, final JavaDoc classJavadoc, final JavaDoc methodJavadoc) {
		List<T> methodTags = methodJavadoc.getTags(clazz);

		if (methodTags.isEmpty()) {
			return classJavadoc.getTags(clazz);
		}

		return methodTags;
	}
}
