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

import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertEquals;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertNotNull;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertTrue;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ACCEPTANCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.COMMA;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREF_READ_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_READ_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_READ_MULTIPLE_ATTRS_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NO_FDNS_FOUND_MSG;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NSS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.READ_OPERATION_DID_NOT_RETURN_EXPECTED_VALUES;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.RFA250;

import java.util.Map;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;

/**
 * This class contains the MO Read acceptance test cases that will be executed as part of CDB/KGB.
 */
public class MoReadTest extends CommonTest {
    private static final String FAILED_TO_GET_ATTRIBUTE = "Null response returned from the CLI for attribute read request.";
    public static final String FAILED_TO_SET_MO_ATTR_ON_NETSIM = "Failed to set attribute on the MO in NetSim";

    /**
     * @DESCRIPTION Test case for the basic MO Read flow.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-13954-001", title = "Acceptance Tests - Verify read of ERBS non-persistent attributes.")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MO_READ_DATASOURCE)
    public void readNonPersistentAttributes(@Input("moType") final String moType, @Input("attributeName") final String attributeName,
            @Input("enmValue") final String enmValue, @Input("nodeValue") final String nodeValue) {
        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        if (isNetsim) {
            assertTrue(FAILED_TO_SET_MO_ATTR_ON_NETSIM, netsimOperator.setManagedObjectAttribute(fdnUnderTest, attributeName, nodeValue));
            final String attributeValueFromCli = writeNodeOperator.getAttribute(fdnUnderTest, attributeName);
            assertNotNull(FAILED_TO_GET_ATTRIBUTE, attributeValueFromCli);
            verifyAttributeValue(attributeValueFromCli, enmValue);
        } else {
            assertNotNull(FAILED_TO_GET_ATTRIBUTE, writeNodeOperator.getAttribute(fdnUnderTest, attributeName));
        }
    }

    /**
     * @DESCRIPTION Test case for verifying the reading of multiple non-persistent and persistent attributes from a MO.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-13954-002", title = "Acceptance Tests - Verify read of multiple persistent and non-persistent MO attributes.")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MO_READ_MULTIPLE_ATTRS_DATASOURCE)
    public void readMultiplePersistentAndNonPersistentMoAttributes(@Input("moType") final String moType,
            @Input("attributeNames") final String attributeNames, @Input("nodeValues") final String nodeValues) {

        // Setup
        final String[] attributesUnderTest = attributeNames.split(COMMA, -1);
        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        if (isNetsim) {
            final Map<String, String> expectedNamesAndValues = writeNodeOperator.parseInputData(attributeNames, nodeValues);
            netsimOperator.setManagedObjectAttributes(fdnUnderTest, expectedNamesAndValues);
            final Map<String, String> attributeValuesFromCli = writeNodeOperator.getAttributes(fdnUnderTest, attributesUnderTest);
            verifyAttributeValues(attributeValuesFromCli, expectedNamesAndValues);

        } else {
            assertNotNull(FAILED_TO_GET_ATTRIBUTE, writeNodeOperator.getAttributes(fdnUnderTest, attributesUnderTest));
        }
    }

    /**
     * @DESCRIPTION Test case for the basic MO Read flow.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-13954-003", title = "Acceptance Tests - Verify read of ERBS moRef type non-persistent attributes.")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MOREF_READ_DATASOURCE)
    public void readMoRefNonPersistentAttributes(@Input("moType") final String moType, @Input("attributeName") final String attributeName,
            @Input("refMoType") final String refMoType) {

        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);

        if (isNetsim) {
            final String referencedFdn = writeNodeOperator.getRandomFdn(networkElementId, refMoType);
            final String moId = netsimOperator.getMoId(referencedFdn);
            assertTrue(FAILED_TO_SET_MO_ATTR_ON_NETSIM, netsimOperator.setManagedObjectAttribute(fdnUnderTest, attributeName, moId));
            final String attributeValueFromCli = writeNodeOperator.getAttribute(fdnUnderTest, attributeName);
            assertNotNull(FAILED_TO_GET_ATTRIBUTE, attributeValueFromCli);
            verifyAttributeValue(attributeValueFromCli.substring(1, attributeValueFromCli.length() - 1), referencedFdn);
        } else {
            assertNotNull(FAILED_TO_GET_ATTRIBUTE, writeNodeOperator.getAttribute(fdnUnderTest, attributeName));
        }
    }

    private void verifyAttributeValues(final Map<String, String> cliAttributes, final Map<String, String> expectedNamesAndValues) {
        for (final String attrName : cliAttributes.keySet()) {
            verifyAttributeValue(cliAttributes.get(attrName), expectedNamesAndValues.get(attrName));
        }
    }

    private void verifyAttributeValue(final String cliAttributeValue, final String expectedValue) {
        assertEquals(READ_OPERATION_DID_NOT_RETURN_EXPECTED_VALUES, cliAttributeValue, expectedValue);
    }
}
