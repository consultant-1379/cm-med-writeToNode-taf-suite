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

import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertEquals;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertThat;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertTrue;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ACCEPTANCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.BATCH_MO_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CLOSE_SQUARE_BRACKET;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.COMMA;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREFLIST_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREF_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NO_FDNS_FOUND_MSG;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NSS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.OPEN_SQUARE_BRACKET;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.RFA250;
import static com.ericsson.oss.testware.enm.cli.matchers.EnmCliResponseMatcher.hasLineContaining;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.oss.testware.network.operators.netsim.NetSimUtil;

/**
 * This class contains the MO Update acceptance test cases that will be executed as part of CDB/KGB.
 */
public class MoUpdateTest extends CommonTest {

    /**
     * @DESCRIPTION Test case for the basic MO update flow.
     *              The data that drives the test case should modify the MO twice with a different attribute value each time. This will ensure that
     *              the data is actually modified with a new value.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8746-001", title = "Acceptance Tests - Verify update MO flow")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MO_UPDATE_DATASOURCE)
    public void verifyMoUpdate(@Input("moType") final String moType, @Input("enmAttributeValuePairs") final String enmAttributeValuePairs,
            @Input("nrmAttributeValuePairs") final String nrmAttributeValuePairs) {

        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        assertThat(writeNodeOperator.updateManagedObject(fdnUnderTest, enmAttributeValuePairs), not(hasLineContaining("Error")));
        verifyAttributesUpdatedInDps(enmAttributeValuePairs, fdnUnderTest);
        if (isNetsim) {
            assertTrue(netsimOperator.isMoUpdated(fdnUnderTest, enmAttributeValuePairs, nrmAttributeValuePairs));
        }
    }

    /**
     * @DESCRIPTION Test case for MO update flow for updating an attribute of MoRef type.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8746-002", title = "FunctionalTests - Verify update MO flow for attributes of type MoRef")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MOREF_UPDATE_DATASOURCE)
    public void verifyMoRefUpdate(@Input("moType") final String moType, @Input("attributeName") final String attributeName,
            @Input("referenceType") final String referencedMoType) {

        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        final String referencedFdn = writeNodeOperator.getRandomFdn(networkElementId, referencedMoType);
        assertThat(writeNodeOperator.updateManagedObject(fdnUnderTest, attributeName, writeNodeOperator.encloseInDoubleQuotes(referencedFdn)),
                not(hasLineContaining("Error")));
        if (isNetsim) {
            assertEquals(netsimOperator.getMoId(referencedFdn), netsimOperator.getAttributeValue(fdnUnderTest, attributeName));
        }

    }

    /**
     * @DESCRIPTION Test case for MO update flow for updating a batch of MOs on a single LTE node.
     *              The data that drives the test case should modify the MO twice with a different attribute value each time. This will ensure that
     *              the data is actually modified with a new value.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8746-003", title = "Acceptance Tests - Verify update MO flow")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = BATCH_MO_UPDATE_DATASOURCE)
    public void verifyBatchMoUpdate(@Input("moType") final String moType, @Input("testCaseAttributes") final String testCaseAttributes,
            @Input("expectedResultAttributes") final String expectedResultAttributes,
            @Input("parentType") final String parentType, @Input("minNumberMos") final int minNumberMos,
            @Input("moAttributes") final String moAttributes) {

        final String parentFdn = writeNodeOperator.getRandomFdn(networkElementId, parentType);
        assertFalse(NO_FDNS_FOUND_MSG, parentFdn.isEmpty());
        final Set<String> createdMos = writeNodeOperator.createMaxChildren(minNumberMos, parentFdn, moType, moAttributes);
        boolean verified = true;
        try {
            assertThat(writeNodeOperator.updateManagedObjects(networkElementId, moType, testCaseAttributes), not(hasLineContaining("Error")));
            if (isNetsim) {
                final Set<String> fdns = writeNodeOperator.getMoFdnsFromSpecifiedNode(networkElementId, moType);
                for (final String fdn : fdns) {
                    if (!netsimOperator.isMoUpdated(fdn, testCaseAttributes, expectedResultAttributes)) {
                        verified = false;
                    }
                }
            }
        } finally {
            writeNodeOperator.deleteManagedObjects(createdMos);
            assertTrue(verified);
        }
    }

    /**
     * @DESCRIPTION Test case for MO update flow for updating an attribute of MoRef List type.
     * @PRE Restful service available
     *      NetSim is available and Nodes are in the correct state
     *      Network Elements are added and synched to the SUT
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8746-004", title = "FunctionalTests - Verify update MO flow for attributes of type MoRef List")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = MOREFLIST_UPDATE_DATASOURCE)
    public void verifyMoRefListUpdate(@Input("moType") final String moType, @Input("attributeName") final String attributeName,
            @Input("referenceTypes") final String referencedMoTypes) {

        final String fdnUnderTest = writeNodeOperator.getRandomFdn(networkElementId, moType);
        assertFalse(NO_FDNS_FOUND_MSG, fdnUnderTest.isEmpty());
        final Set<String> referencedFdns = writeNodeOperator.getMoFdnsFromSpecifiedNode(networkElementId,
                Arrays.asList(referencedMoTypes.split(COMMA)));
        assertThat(writeNodeOperator.updateManagedObject(fdnUnderTest, attributeName, convertToCliSyntax(referencedFdns)),
                not(hasLineContaining("Error")));

        if (isNetsim) {
            final String netsimValue = netsimOperator.getAttributeValue(fdnUnderTest, attributeName);
            final String[] netsimValues = netsimValue.split(COMMA);
            final Iterator<String> referncedFdnIterator = referencedFdns.iterator();
            for (int i = 0; i < netsimValues.length; i++) {
                final String moId = netsimOperator.getMoId(referncedFdnIterator.next());
                assertEquals(moId, netsimValues[i]);
            }
        }
    }

    private void verifyAttributesUpdatedInDps(final String expectedAttributeValuePairs, final String fdnUnderTest) {
        final Map<String, String> expectedTestCaseAttrList = NetSimUtil.getAttributesFromCommand(expectedAttributeValuePairs);
        final Set<String> expectedAttributeNames = expectedTestCaseAttrList.keySet();
        final Map<String, String> actualAttrList = writeNodeOperator.getAttributes(fdnUnderTest,
                expectedAttributeNames.toArray(new String[expectedAttributeNames.size()]));
        final String regexParseWords = "[\\W&&[^,]]";
        for (final String expectedAttrName : expectedAttributeNames) {
            final String expectedValue = expectedTestCaseAttrList.get(expectedAttrName).replaceAll(regexParseWords, "");
            final String retrievedValue = actualAttrList.get(expectedAttrName).replaceAll(regexParseWords, "");
            final List<String> attributeListExpected = Arrays.asList(expectedValue.split(COMMA));
            final List<String> attributeListRetrieved = Arrays.asList(retrievedValue.split(COMMA));
            for (final String attributeVal : attributeListExpected) {
                assertTrue("Attribute not found in list", attributeListRetrieved.contains(attributeVal));
            }
        }
    }

    private String convertToCliSyntax(final Set<String> referencedFdns) {
        final StringBuilder sb = new StringBuilder();
        sb.append(OPEN_SQUARE_BRACKET);
        for (final String fdn : referencedFdns) {
            sb.append(writeNodeOperator.encloseInDoubleQuotes(fdn)).append(COMMA);
        }
        sb.replace(sb.length() - 1, sb.length(), CLOSE_SQUARE_BRACKET);
        return sb.toString();
    }

}
