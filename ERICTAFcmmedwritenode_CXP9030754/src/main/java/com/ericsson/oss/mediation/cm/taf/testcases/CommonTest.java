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

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromCsv;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ACCEPTANCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.BATCH_MO_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CREATE_MO_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CREATE_RADIO_DATA_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.CSV;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.DATA;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.DELETE_RADIO_DATA_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ENM_PASSWORD;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ENM_USER;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREFLIST_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREF_READ_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MOREF_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_ACTION_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_READ_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_READ_MULTIPLE_ATTRS_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.MO_UPDATE_DATASOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NETWORK_ELEMENT_ID;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.NSS;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.REAL_NODE_TESTING;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.RFA250;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.SKIP_ADD_SYNC;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.SKIP_DELETE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.USER_DATSOURCE;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.WRITE_DOOZER;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.ADDED_NODES;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.AVAILABLE_USERS;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.NODES_TO_ADD;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.USERS_TO_CREATE;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.USERS_TO_DELETE;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.USER_TO_CLEAN_UP;
import static com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows.EnmObjectType.USER;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.handlers.netsim.commands.NetSimCommands;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarios;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.oss.mediation.cm.taf.datasources.AvailableUsers;
import com.ericsson.oss.mediation.cm.taf.netsim.NetSimOperatorProvider;
import com.ericsson.oss.mediation.cm.taf.operator.WriteNodeOperator;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.enmbase.data.NetworkNode;
import com.ericsson.oss.testware.network.operators.netsim.NetsimOperator;
import com.ericsson.oss.testware.nodeintegration.exceptions.NodeIntegrationOperatorException;
import com.ericsson.oss.testware.nodeintegration.flows.NodeIntegrationFlows;
import com.ericsson.oss.testware.nodeintegration.operators.impl.NodeSupervisionOperator;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.ericsson.oss.testware.security.authentication.operators.LoginLogoutRestOperator;
import com.ericsson.oss.testware.security.authentication.tool.TafToolProvider;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;
import com.google.inject.Provider;

public class CommonTest extends TafTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTest.class);
    private static final String TEST_STEP_ID_SET_SYS_PROPERTY_FOR_NE = "Set System Property for id of node to be added";
    private static final String RESTORE_NE_STATE_IN_NETSIM = "Restore Network Element state in netsim";
    private static final String DISABLE_SUPERVISION = "disable supervision";

    @Inject
    private GimCleanupFlows idmCleanupFlows;
    @Inject
    private UserManagementTestFlows userManagementFlows;
    @Inject
    private NodeIntegrationFlows nodeIntegrationFlows;
    @Inject
    private TestContext context;
    @Inject
    private Provider<LoginLogoutRestOperator> loginOperatorProvider;
    @Inject
    protected WriteNodeOperator writeNodeOperator;
    @Inject
    protected LoginLogoutRestFlows loginLogoutFlows;
    @Inject
    private NodeSupervisionOperator nodeSupervisionOperator;
    @Inject
    private TafToolProvider tafToolProvider;
    protected NetsimOperator netsimOperator;
    protected boolean isNetsim;
    protected String networkElementId;
    protected HttpTool httpTool;
    private boolean isUserLoggedIn;

    @BeforeSuite(groups = { ACCEPTANCE, RFA250, NSS }, alwaysRun = true)
    public void addAndSynchNode() {
        CommonDataSources.initializeDataSources();
        initializeTestCaseDataSources();
        initializeAvailableUsersDataSource();
        initNetSim();
        if (!REAL_NODE_TESTING && !SKIP_ADD_SYNC) {
            startScenario(getAddAndSyncNodeScenario());
        }
    }

    @BeforeClass(groups = { ACCEPTANCE, RFA250, NSS }, alwaysRun = true)
    public void commonTestSetup() {
        login();
        initNetSim();
        writeNodeOperator.setHttpTool(httpTool);
        networkElementId = (String) DataHandler.getAttribute(NETWORK_ELEMENT_ID);

    }

    @AfterClass(groups = { ACCEPTANCE, RFA250, NSS }, alwaysRun = true)
    public void logOut() {
        if (isUserLoggedIn) {
            LOGGER.info("Logging out.");
            loginOperatorProvider.get().logout(httpTool);
        }
    }

    @AfterSuite(groups = { ACCEPTANCE, RFA250, NSS }, alwaysRun = true)
    public void deleteNodesAndUser() {
        try {
            if (!SKIP_DELETE) {
                startScenario(getDeleteNodeScenario());
            }
        } finally {
            if (DataHandler.getAttribute(ENM_USER).equals(WRITE_DOOZER)) {
                startScenario(getDeleteEnmUserScenario());
            }
        }
    }

    /**
     * Sets a system property containing the id of the added node. Required to
     * enable the suite to execute against 5K sims on vApps or the 15K sims used
     * in Maintrack. Will only work as long as the suite requires only a single
     * node for test. If any more nodes are required, it will be necessary to
     * convert the full suite to use TAF scenarios and flows.
     * Using a system property rather than simply setting it to a class variable
     * allows us to move the adding of the node to the @BeforeSuite method.
     *
     * @param node
     *            Takes
     *            {@link com.ericsson.enm.data.CommonDataSources#NODES_TO_ADD}
     *            Data Source using fields described by
     *            {@link com.ericsson.enm.data.NetworkNode} class
     **/
    @TestStep(id = TEST_STEP_ID_SET_SYS_PROPERTY_FOR_NE)
    public void addNode(@Input(NODES_TO_ADD) final NetworkNode node) {
        LOGGER.info("Setting system property {} to NE {}", NETWORK_ELEMENT_ID, node.getNetworkElementId());
        DataHandler.setAttribute(NETWORK_ELEMENT_ID, node.getNetworkElementId());
    }

    private void initializeTestCaseDataSources() {
        context.addDataSource(USERS_TO_CREATE, fromCsv(DATA + USER_DATSOURCE + CSV));
        context.addDataSource(USERS_TO_DELETE, fromCsv(DATA + USER_DATSOURCE + CSV));
        context.addDataSource(USER_TO_CLEAN_UP, fromCsv(DATA + USER_DATSOURCE + CSV));
        context.addDataSource(CREATE_MO_DATASOURCE, fromCsv(DATA + CREATE_MO_DATASOURCE + CSV));
        context.addDataSource(MO_ACTION_DATASOURCE, fromCsv(DATA + MO_ACTION_DATASOURCE + CSV));
        context.addDataSource(CREATE_RADIO_DATA_DATASOURCE, fromCsv(DATA + CREATE_RADIO_DATA_DATASOURCE + CSV));
        context.addDataSource(DELETE_RADIO_DATA_DATASOURCE, fromCsv(DATA + DELETE_RADIO_DATA_DATASOURCE + CSV));
        context.addDataSource(MO_READ_DATASOURCE, fromCsv(DATA + MO_READ_DATASOURCE + CSV));
        context.addDataSource(MOREF_READ_DATASOURCE, fromCsv(DATA + MOREF_READ_DATASOURCE + CSV));
        context.addDataSource(MO_READ_MULTIPLE_ATTRS_DATASOURCE, fromCsv(DATA + MO_READ_MULTIPLE_ATTRS_DATASOURCE + CSV));
        context.addDataSource(MO_UPDATE_DATASOURCE, fromCsv(DATA + MO_UPDATE_DATASOURCE + CSV));
        context.addDataSource(MOREF_UPDATE_DATASOURCE, fromCsv(DATA + MOREF_UPDATE_DATASOURCE + CSV));
        context.addDataSource(MOREF_UPDATE_DATASOURCE, fromCsv(DATA + MOREF_UPDATE_DATASOURCE + CSV));
        context.addDataSource(MOREFLIST_UPDATE_DATASOURCE, fromCsv(DATA + MOREFLIST_UPDATE_DATASOURCE + CSV));
        context.addDataSource(BATCH_MO_UPDATE_DATASOURCE, fromCsv(DATA + BATCH_MO_UPDATE_DATASOURCE + CSV));
    }

    private void initializeAvailableUsersDataSource() {
        checkLoginUserSystemProperty();
        context.addDataSource(AVAILABLE_USERS, TafDataSources.fromClass(AvailableUsers.class));
    }

    private void checkLoginUserSystemProperty() {
        if (DataHandler.getAttribute(ENM_USER).equals(WRITE_DOOZER)) {
            startScenario(getCreateEnmUserScenario());
            LOGGER.info("User creation was successful. Test suite will use user [{}] for test execution.", WRITE_DOOZER);
        }
    }

    private void login() {
        final String user = (String) DataHandler.getAttribute(ENM_USER);
        final String password = (String) DataHandler.getAttribute(ENM_PASSWORD);
        LOGGER.info("Logging in with user [{}], and password [{}]", user, password);
        httpTool = loginOperatorProvider.get().login(user, password);
        isUserLoggedIn = true;
    }

    private void initNetSim() {
        if (!REAL_NODE_TESTING) {
            isNetsim = true;
            netsimOperator = NetSimOperatorProvider.getInstance().getNetsimOperator();
        }
    }

    private TestScenario getCreateEnmUserScenario() {
        return TestScenarios.scenario()
                .addFlow(idmCleanupFlows.cleanUp(USER))
                .addFlow(userManagementFlows.createUser())
                .build();
    }

    private TestScenario getAddAndSyncNodeScenario() {
        return TestScenarios.scenario()
                .addFlow(loginLogoutFlows.login())
                .addFlow(
                        flow("add and synch node flow")
                                .addTestStep(annotatedMethod(this, TEST_STEP_ID_SET_SYS_PROPERTY_FOR_NE))
                                .addTestStep(annotatedMethod(this, RESTORE_NE_STATE_IN_NETSIM))
                                .addSubFlow(nodeIntegrationFlows.addNode())
                                .addSubFlow(nodeIntegrationFlows.syncNode())
                                .addSubFlow(disableSupervision()) // Due to netsim restore there is a
                                                                  // mismatch between the enm system and the netsim.
                                                                  // A re-synch is required.
                                .addSubFlow(nodeIntegrationFlows.syncNode())
                                .withDataSources(dataSource(NODES_TO_ADD)))
                .addFlow(loginLogoutFlows.logout())
                .build();
    }

    private TestScenario getDeleteNodeScenario() {
        return TestScenarios.scenario()
                .addFlow(loginLogoutFlows.login())
                .addFlow(flow("delete nodes flow").addSubFlow(nodeIntegrationFlows.deleteNode()).withDataSources(dataSource(ADDED_NODES)))
                .addFlow(loginLogoutFlows.logout())
                .build();
    }

    private TestScenario getDeleteEnmUserScenario() {
        return TestScenarios.scenario()
                .addFlow(userManagementFlows.deleteUser())
                .build();
    }

    private void startScenario(final TestScenario scenario) {
        TestScenarios.runner().withListener(new LoggingScenarioListener()).build().start(scenario);
    }

    @TestStep(id = RESTORE_NE_STATE_IN_NETSIM)
    public void restoreNeState(@Input(NODES_TO_ADD) final NetworkNode node) {
        final NetworkElement networkElement = netsimOperator.getNetworkElement(node.getNetworkElementId());
        final String restoreImagePath =
                String.format("/netsim/netsimdir/%s/allsaved/dbs/%s_%s", networkElement.getSimulationName(), "curr", networkElement.getName());
        LOGGER.info("Restoring state of {} from {}", networkElement.getName(), restoreImagePath);

        networkElement.exec(NetSimCommands.stop());
        networkElement.exec(NetSimCommands.restorenedatabase(restoreImagePath));
        networkElement.exec(NetSimCommands.start());
    }

    @TestStep(id = DISABLE_SUPERVISION)
    public void disableSupervision(@Input(NODES_TO_ADD) final NetworkNode node) throws NodeIntegrationOperatorException {
        LOGGER.info("Disabling supervision on node " + node.getNetworkElementId());
        nodeSupervisionOperator.disableSupervisionOnNode(node.getNetworkElementId(), tafToolProvider.getHttpTool());
    }

    private TestStepFlow disableSupervision() {
        return flow("Disable Supervision")
                .addTestStep(annotatedMethod(this, DISABLE_SUPERVISION))
                .withDataSources(dataSource(NODES_TO_ADD))
                .build();

    }

}
