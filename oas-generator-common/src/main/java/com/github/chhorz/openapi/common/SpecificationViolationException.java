package com.github.chhorz.openapi.common;

/**
 * This exception will be thrown if the openapi specification will be violated at some point. There may be some reasons
 * for the violation:
 * <ul>
 *     <li>Missing required field</li>
 * </ul>
 *
 * @author chhorz
 */
public class SpecificationViolationException extends RuntimeException {

	/**
	 * The openapi specification was violated because of the given reason.
	 *
	 * @param message the reason of the violation
	 */
	public SpecificationViolationException(String message) {
		super(message);
	}

}
