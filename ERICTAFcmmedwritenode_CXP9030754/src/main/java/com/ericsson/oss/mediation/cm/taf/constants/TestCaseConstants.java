/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.cm.taf.constants;

public class TestCaseConstants {

    private TestCaseConstants() {}

    public static final String OSS_PREFIX = "ossPrefix";
    public static final String NETWORK_ELEMENT = "NetworkElement";

    public static final String USER_DATSOURCE = "userToCreate";
    public static final String CREATE_MO_DATASOURCE = "createMo";
    public static final String CREATE_RADIO_DATA_DATASOURCE = "createRadioData";
    public static final String DELETE_RADIO_DATA_DATASOURCE = "deleteRadioData";
    public static final String MO_ACTION_DATASOURCE = "moAction";
    public static final String MO_READ_DATASOURCE = "moRead";
    public static final String MOREF_READ_DATASOURCE = "moRefRead";
    public static final String MO_READ_MULTIPLE_ATTRS_DATASOURCE = "moReadMultipleAttributes";
    public static final String MOREF_UPDATE_DATASOURCE = "moRefUpdate";
    public static final String MOREFLIST_UPDATE_DATASOURCE = "moRefListUpdate";
    public static final String MO_UPDATE_DATASOURCE = "moUpdate";
    public static final String BATCH_MO_UPDATE_DATASOURCE = "batchMoUpdate";

    public static final String WRITE_DOOZER = "WriteDoozer";
    public static final String SHROOT12 = "Shroot12";
    public static final String ENM_USER = "enm.user";
    public static final String ENM_PASSWORD = "enm.password";

    public static final String NO_FDNS_FOUND_MSG = "No suitable FDNs found on the SUT, was there a problem with node synch?";
    public static final String READ_OPERATION_DID_NOT_RETURN_EXPECTED_VALUES = "Read operation did not return expected values";
    public static final String COULD_NOT_COMMIT_TRANSACTION = "Could not commit transaction";
    public static final String OBJECT_FAILED_CONTENT_VERIFICATION = "object failed content verification";

    public static final String INSTALL_ACTION = "install";
    public static final String MANUALRESTART_ACTION = "manualRestart";
    public static final String MANUALRESTART_ACTION_PARAMS =
            "restartRank=RESTART_WARM,restartReason=UNPLANNED_NODE_UPGRADE_PROBLEMS,restartInfo=some";
    public final static String ATTR_MODIFIED_BY_INSTALL_ACTION = "actionResult";
    public final static String USER_LABEL = "userLabel";

    public final static String COMMA = ",";
    public final static String EQUALS = "=";
    public final static String OPEN_SQUARE_BRACKET = "[";
    public final static String CLOSE_SQUARE_BRACKET = "]";
    public final static String SEMI_COLON = ";";
    public static final String DATA = "data/";
    public static final String CSV = ".csv";

    public final static String USER_LABEL_EQUALS = USER_LABEL + EQUALS;
    public final static int NUM_SYNC_STATUS_CHECKS = 50;
    public final static int TIME_BETWEEN_SYNC_STATUS_CHECKS = 5000;

    public static final String NETWORK_ELEMENT_ID = "network.element.id";
    public static final boolean REAL_NODE_TESTING = Boolean.getBoolean("real.node.testing");
    public static final boolean SKIP_ADD_SYNC = Boolean.getBoolean("skip.add.sync");
    public static final boolean SKIP_DELETE = Boolean.getBoolean("skip.delete");

    public static final String ACCEPTANCE = "Acceptance";
    public static final String RFA250 = "RFA250";
    public static final String NSS = "NSS";
}
