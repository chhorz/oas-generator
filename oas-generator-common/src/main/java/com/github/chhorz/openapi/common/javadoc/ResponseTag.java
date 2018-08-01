package com.github.chhorz.openapi.common.javadoc;

import com.github.chhorz.javadoc.tags.StructuredTag;

public class ResponseTag extends StructuredTag {

	private static final String TAG_NAME = "response";

	private static final String STATUS_CODE = "statusCode";
	private static final String RESPONSE_TYPE = "responseType";
	// private static final String DESCRIPTION = "description";

	public ResponseTag() {
		super(TAG_NAME, STATUS_CODE, RESPONSE_TYPE/* , DESCRIPTION */);
	}

	public String getStatusCode() {
		return getValues().get(STATUS_CODE);
	}

	public String getResponseType() {
		return getValues().get(RESPONSE_TYPE);
	}

	// public String getDescription() {
	// return getValues().get(DESCRIPTION);
	// }

}
