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
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.ID;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ACCEPTANCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.COMMA;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.COULD_NOT_COMMIT_TRANSACTION;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CREATE_MO_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CREATE_RADIO_DATA_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.DELETE_RADIO_DATA_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.EQUALS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NETWORK_ELEMENT;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NO_FDNS_FOUND_MSG;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NSS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.OBJECT_FAILED_CONTENT_VERIFICATION;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.OSS_PREFIX;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.RFA250;
import static com.ericsson.oss.testware.enm.cli.matchers.EnmCliResponseMatcher.hasLineContaining;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.oss.mediation.cm.taf.util.VersantHelper;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;

public class CreateDeleteMoTest extends CommonTest {

    @Inject
    VersantHelper versantHelper;

    private int numObjects;
    private final Logger logger = LoggerFactory.getLogger(CreateDeleteMoTest.class);
    private final List<String> undeletedMoFdns = new ArrayList<>();

    @AfterClass(groups = { ACCEPTANCE, RFA250, NSS }, alwaysRun = true)
    public void cleanUp() {
        removeLeftoverMos();
    }

    /**
     * @DESCRIPTION Verifies basic MO Create and Delete on the node. Test case verifies the creation and deletion by finding the
     *              MO on the NetSim post creation, and not finding it on the NetSim post deletion.
     * @PRE Restful service available.
     *      NetSim is available and Nodes are in the correct state.
     *      Network Elements are added and synched to the SUT.
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8745-001", title = "Acceptance Tests - Verify MO Created on the node")
    @Test(groups = { ACCEPTANCE, NSS }, enabled = true)
    @DataDriven(name = CREATE_MO_DATASOURCE)
    public void testCreateAndDeleteMo(@Input("moType") final String moType, @Input("moId") final String moId,
            @Input("parentType") final String parentType, @Input("attributes") final String attributes) {
        checkForExistingMoFromFailedTestRuns(moType, moId);
        final String parentFdn = writeNodeOperator.getRandomFdn(networkElementId, parentType);
        assertFalse(NO_FDNS_FOUND_MSG, parentFdn.isEmpty());
        final String fdnUnderTest = getChildFdn(parentFdn, moType, moId);
        createMoAndVerifyCreation(moType, moId, attributes, fdnUnderTest);
        deleteMoAndVerifyDeletion(moType, fdnUnderTest, moId);
    }

    /**
     * @DESCRIPTION Verifies the creation of radio data on the node.
     * @PRE Restful service available.
     *      NetSim is available and Nodes are in the correct state.
     *      Network Elements are added and synched to the SUT.
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8745-002", title = "Acceptance Tests - Verify Radio data can be created")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = CREATE_RADIO_DATA_DATASOURCE)
    public void createRadioData(@Input("ldn") final String ldn, @Input("attributes") final String attributes) {
        final String OSS_PREFIX_PATTERN = "${ossPrefix}";
        final String ossPrefix = writeNodeOperator.getAttribute(networkElementId, NETWORK_ELEMENT, networkElementId, OSS_PREFIX);
        final String fdnUnderTest = ossPrefix + COMMA + ldn;
        final String updatedAttributes = attributes.replace(OSS_PREFIX_PATTERN, ossPrefix);

        // Execution
        EnmCliResponse response = writeNodeOperator.createManagedObject(fdnUnderTest, updatedAttributes);

        // Retry mechanism to prevent test failing due to optimistic lock exception
        for (int i = 0; i < 2; i++) {
            if (response.toString().contains(COULD_NOT_COMMIT_TRANSACTION) ||
                    response.toString().contains(OBJECT_FAILED_CONTENT_VERIFICATION)) {
                logger.info("Response received: {}. Retrying Create operation", response.toString());
                response = writeNodeOperator.createManagedObject(fdnUnderTest, updatedAttributes);
                logVersantDetailsIfLastRetryFails(response, i);
            }
        }

        // Verification
        assertThat(response, not(hasLineContaining("Error")));
        assertFalse(writeNodeOperator.getPersistentAttributes(fdnUnderTest).isEmpty());
    }

    /**
     * @DESCRIPTION Verifies the deletion of radio data on the node.
     * @PRE Restful service available.
     *      NetSim is available and Nodes are in the correct state.
     *      Network Elements are added and synched to the SUT.
     *      TORF-8745-002 executed successfully.
     * @PRIORITY BLOCKER
     */
    @TestId(id = "TORF-8745-003", title = "Acceptance Tests - Verify Radio data can be deleted")
    @Test(groups = { ACCEPTANCE, RFA250, NSS }, enabled = true)
    @DataDriven(name = DELETE_RADIO_DATA_DATASOURCE)
    public void deleteRadioData(@Input("ldn") final String ldn) {
        final String ossPrefix = writeNodeOperator.getAttribute(networkElementId, NETWORK_ELEMENT, networkElementId, OSS_PREFIX);
        final String fdnUnderTest = ossPrefix + COMMA + ldn;
        addFdnToListOfNetSimMosToBeCleanedUp(fdnUnderTest);

        // Execution
        EnmCliResponse response = writeNodeOperator.deleteManagedObject(fdnUnderTest);

        // Retry mechanism to prevent test failing due to optimistic lock exception
        for (int i = 0; i < 2; i++) {
            if (response.toString().contains(COULD_NOT_COMMIT_TRANSACTION) ||
                    response.toString().contains(OBJECT_FAILED_CONTENT_VERIFICATION)) {
                logger.info("Response received: {}. Retrying Delete operation", response.toString());
                response = writeNodeOperator.deleteManagedObject(fdnUnderTest);
                logVersantDetailsIfLastRetryFails(response, i);
            }
        }

        // Verification
        assertTrue(response.isCommandSuccessful());
        assertThat(response, not(hasLineContaining("Error")));
        assertThat(response, not(hasLineContaining("0 instance(s) found")));
        assertThat(response, not(hasLineContaining(":0 instance(s) deleted")));
        assertThat(response, hasLineContaining("instance(s) deleted"));
        if (isNetsim) {
            assertFalse(netsimOperator.isMoCreated(fdnUnderTest));
        }
        removeFdnFromListOfNetSimMosToBeCleanedUp(fdnUnderTest);
    }

    private String getChildFdn(final String parentFdn, final String moType, final String moId) {
        final StringBuilder sb = new StringBuilder(parentFdn);
        sb.append(COMMA);
        sb.append(moType);
        sb.append(EQUALS);
        sb.append(moId);
        return sb.toString();
    }

    private void createMoAndVerifyCreation(final String moType, final String moId, final String attributes, final String fdnUnderTest) {
        numObjects = writeNodeOperator.getMoFdnsFromSpecifiedNode(networkElementId, moType).size();
        assertThat(writeNodeOperator.createManagedObject(fdnUnderTest, attributes), not(hasLineContaining("Error")));
        assertEquals(moId, writeNodeOperator.getAttribute(fdnUnderTest, moType + ID));
        assertEquals(writeNodeOperator.getMoFdnsFromSpecifiedNode(networkElementId, moType).size(), numObjects + 1);

        if (isNetsim) {
            final boolean moCreated = netsimOperator.isMoCreated(fdnUnderTest);
            assertTrue("The mo " + fdnUnderTest + " was not created", moCreated);
        }
    }

    private void deleteMoAndVerifyDeletion(final String moType, final String fdnUnderTest, final String moId) {
        assertThat(writeNodeOperator.deleteManagedObject(fdnUnderTest), not(hasLineContaining("Error")));
        assertEquals(writeNodeOperator.getMoFdnsFromSpecifiedNode(networkElementId, moType).size(), numObjects);
        if (isNetsim) {
            final boolean moDeleted = !netsimOperator.isMoCreated(fdnUnderTest);
            assertTrue("The mo " + fdnUnderTest + " was not deleted", moDeleted);
        }
    }

    private void checkForExistingMoFromFailedTestRuns(final String moType, final String moId) {
        final String fdn = writeNodeOperator.getMoFdn(moType, moId);
        if (fdn != null && !fdn.isEmpty()) {
            writeNodeOperator.deleteManagedObject(fdn);
        }
    }

    private void logVersantDetailsIfLastRetryFails(final EnmCliResponse response, final int retryAttempt) {
        if (response.toString().contains(COULD_NOT_COMMIT_TRANSACTION) && retryAttempt == 1) {
            versantHelper.initConnectionToActiveDbHost();
            versantHelper.printLockDetailsToFile();
            versantHelper.printDbContentsToFile();
            versantHelper.closeConnection();
        }
    }

    private void addFdnToListOfNetSimMosToBeCleanedUp(final String fdn) {
        undeletedMoFdns.add(fdn);
    }

    private void removeFdnFromListOfNetSimMosToBeCleanedUp(final String fdn) {
        undeletedMoFdns.remove(fdn);
    }

    private void removeLeftoverMos() {
        logger.info("Executing against {}", isNetsim ? "Netsim" : "Real Node");
        if (isNetsim && !undeletedMoFdns.isEmpty()) {
            logger.info("The following MOs have not been deleted and will now be removed: [{}]", undeletedMoFdns);
            for (final String fdn : undeletedMoFdns) {
                final boolean isDeletionSuccessful = netsimOperator.deleteManagedObject(fdn);
                logger.info("Deletion of MO with fdn [{}] - Success? {}", fdn, isDeletionSuccessful);
            }
        }
    }

}
