/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.cm.taf.testcases;

import static org.hamcrest.core.IsNot.not;

import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertNotEquals;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertThat;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ACCEPTANCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_ACTION_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NO_FDNS_FOUND_MSG;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NSS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.RFA250;
import static com.ericsson.oss.testware.enm.cli.matchers.EnmCliResponseMatcher.hasLineContaining;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;

/**
 * This class contains the MO Action acceptance test cases that will be executed as part of CDB/KGB.
 */
public class MoActionTest extends CommonTest {

    /**
     * @DESCRIPTION Test case for verifying the invocation of an action on an MO. Test case verifies the action was successful by checking the
     *              {@code verificationAttribute} to ensure the action was executed successfully.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-9592-001", title = "Acceptance Tests - Verify MO action")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MO_ACTION_DATASOURCE)
    public void testAction(@Input("moType") final String moType, @Input("actionName") final String action,
            @Input("parameters") final String parameters, @Input("verificationAttribute") final String verificationAttribute,
            @Input("cleanupAction") final String cleanupAction, @Input("cleanupParameters") final String cleanupParameters) {
        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        try {
            if (isNetsim) {
                final String attributeValueBeforeAction = getNetsimAttributeValue(fdnUnderTest, verificationAttribute);
                assertThat(writeNodeOperator.executeActionOnMo(fdnUnderTest, action, parameters),
                        not(hasLineContaining("Error")));
                final String attributeValueAfterAction = getNetsimAttributeValue(fdnUnderTest, verificationAttribute);
                verifyNetsimActionInvoked(attributeValueBeforeAction, attributeValueAfterAction);
            } else {
                assertThat(writeNodeOperator.executeActionOnMo(fdnUnderTest, action, parameters), not(hasLineContaining("Error")));
            }
        } finally {
            if (cleanupAction != null && !cleanupAction.equals("null")) {
                writeNodeOperator.executeActionOnMo(fdnUnderTest, cleanupAction, cleanupParameters);
            }
        }
    }

    /*
     * Gets the Attribute Value from the NetSim. In the case of Real Node testing this returns an empty String.
     */
    private String getNetsimAttributeValue(final String fdnUnderTest, final String attribute) {
        return netsimOperator.getAttributeValue(fdnUnderTest, attribute);
    }

    /*
     * Verifies that the Verification Attribute has changed on the NetSim. In the case of Real Node testing this does no check.
     */
    private void verifyNetsimActionInvoked(final String verificationAttributeBefore, final String verificationAttributeAfter) {
        assertNotEquals(verificationAttributeBefore, verificationAttributeAfter);
    }
}
