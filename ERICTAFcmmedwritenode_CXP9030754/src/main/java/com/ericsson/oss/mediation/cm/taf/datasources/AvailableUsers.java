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
package com.ericsson.oss.mediation.cm.taf.datasources;

import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ENM_PASSWORD;
import static com.ericsson.oss.mediation.cm.taf.constants.TestCaseConstants.ENM_USER;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.ericsson.cifwk.taf.annotations.DataSource;
import com.ericsson.cifwk.taf.data.DataHandler;

/**
 * Initializes the {@code com.ericsson.enm.data.CommonDataSources.AVAILABLE_USERS} datasource.
 */
public class AvailableUsers {

    private final static Logger LOGGER = LoggerFactory.getLogger(AvailableUsers.class);

    @DataSource
    public List<Map<String, Object>> users() {
        
        final List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        final String enmUser = (String) DataHandler.getAttribute(ENM_USER);
        final String enmUserPassword = (String) DataHandler.getAttribute(ENM_PASSWORD);

        if (enmUser != null && enmUserPassword != null) {
            addTheUser(users, enmUser, enmUserPassword);
            LOGGER.info("Adding user [{}] with password [{}] to the availableUsers datasource", enmUser, enmUserPassword);
        }
        return users;
    }

    private void addTheUser(final List<Map<String, Object>> users, final String enmUser, final String enmUserPassword) {
        
        final Map<String, Object> user = new HashMap<String, Object>();
        user.put("username", enmUser);
        user.put("password", enmUserPassword);
        users.add(user);
    }
}
