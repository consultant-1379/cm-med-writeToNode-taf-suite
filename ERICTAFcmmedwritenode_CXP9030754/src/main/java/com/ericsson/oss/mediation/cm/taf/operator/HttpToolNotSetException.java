/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.cm.taf.operator;

public class HttpToolNotSetException extends RuntimeException {
    private static final String HTTPTOOL_NOT_SET_RSP = "The operator cannot be used as the Httptool has not been set via the setHttpTool method.";

    public HttpToolNotSetException() {
        super(HTTPTOOL_NOT_SET_RSP);
    }
}
