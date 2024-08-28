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

package com.ericsson.oss.mediation.cm.taf.util;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper.HopBuilder;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

@Singleton
public class VersantHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersantHelper.class);
    private static final String CONNECTION_NOT_INITIALIZED = "Could not perform operation as the connection to the DB host was never initialized.";

    private CLICommandHelper dbCli;

    /**
     * Performs lazy initialization of the class, establishing an SSH connection
     * to the active DB server. This is required before methods on the class can
     * be used.
     */
    public synchronized void initConnectionToActiveDbHost() {
        if (dbCli == null) {
            LOGGER.info("Initializing connection to db1.");
            setCliForHost(HostConfigurator.getDb1());
            if (dbCli == null || !isVersantRunning(dbCli)) {
                LOGGER.info("Versant is not running on db1, assuming db2 is active.");
                setCliForHost(HostConfigurator.getDb2());
            }
        } else {
            logWarning("Connection was already initialized.");
        }
    }

    public void printDbContentsToFile() {
        if (dbCli != null) {
            final String db2ttyListAllInstances = "/ericsson/versant/bin/db2tty -D dps_integration -i > /var/tmp/moInstances.txt";
            dbCli.execute(db2ttyListAllInstances);
        } else {
            logWarning(CONNECTION_NOT_INITIALIZED);
        }
    }

    public void printLockDetailsToFile() {
        if (dbCli != null) {
            final String basicLockInfo = "/ericsson/versant/bin/dbtool -locks -info dps_integration > /var/tmp/lockInfo.txt";
            final String verboseLockInfo = "/ericsson/versant/bin/dbtool -locks -table dps_integration > /var/tmp/lockTableInfo.txt";
            dbCli.execute(basicLockInfo);
            dbCli.execute(verboseLockInfo);
        } else {
            logWarning(CONNECTION_NOT_INITIALIZED);
        }
    }

    public void closeConnection() {
        dbCli.disconnect();
        dbCli = null;
    }

    private void setCliForHost(final Host host) {
        try {
            final User rootUser = new User("root", "12shroot", UserType.ADMIN);
            final User versantUser = new User("versant", "12shroot", UserType.ADMIN);
            final HopBuilder hop = new CLICommandHelper(HostConfigurator.getMS()).newHopBuilder();
            dbCli = hop.hopWithKeyFile(host, HostConfigurator.getKeyFile()).hop(rootUser).hop(versantUser).build();
        } catch (final Exception e) {
            LOGGER.debug("Exception occurred {}", e);
            logWarning("Failed to get connection to the database host - " + host.getHostname());
            dbCli = null;
        }
    }

    private boolean isVersantRunning(final CLICommandHelper dbCli) {
        final String response = dbCli.execute("/ericsson/versant/bin/dblist");
        LOGGER.debug("Resonse received from dblist command {}", response);
        return !response.contains("UT_ER_DBID_NOACCESS");
    }

    private void logWarning(final String msg) {
        LOGGER.warn(msg);
    }
}
