package com.github.chhorz.openapi.common.javadoc;

import com.github.chhorz.javadoc.tags.StructuredTag;

/**
 * Custom Javadoc tag for structured parsing.
 *
 * @see com.github.chhorz.javadoc.JavaDocParser
 *
 * @author chhorz
 */
public class ResponseTag extends StructuredTag {

	private static final String TAG_NAME = "response";

	private static final String STATUS_CODE = "statusCode";
	private static final String RESPONSE_TYPE = "responseType";
	private static final String DESCRIPTION = "description";

	/**
	 * Represents a tag in the following form:
	 *
	 * @response 200 String the content as string
	 */
	public ResponseTag() {
		super(TAG_NAME, STATUS_CODE, RESPONSE_TYPE , DESCRIPTION);
	}

	public String getStatusCode() {
		return getValues().get(STATUS_CODE);
	}

	public String getResponseType() {
		return getValues().get(RESPONSE_TYPE);
	}

	public String getDescription() {
		return getValues().get(DESCRIPTION);
	}

}
