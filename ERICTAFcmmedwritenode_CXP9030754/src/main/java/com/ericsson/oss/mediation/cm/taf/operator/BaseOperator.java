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

import static com.ericsson.oss.mediation.cm.taf.constants.Constants.COMMA;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.DOUBLE_QUOTE;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.EMPTY_STRING;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.LOG_RESPONSE;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.oss.testware.enm.cli.EnmCliOperator;
import com.ericsson.oss.testware.enm.cli.EnmCliOperatorImpl;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;
import com.ericsson.oss.testware.nodeintegration.utilities.CmEditorCommandUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public abstract class BaseOperator {

    private final Set<String> deleteFdnList = new LinkedHashSet<String>();
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOperator.class);
    protected boolean isHttpToolSet = false;
    private HttpTool httpTool;

    @Inject
    Provider<EnmCliOperatorImpl> provider;

    EnmCliOperator enmCliOperator;

    protected EnmCliOperator getEnmCliOperator() {
        if (enmCliOperator == null) {
            enmCliOperator = provider.get();
        }
        return enmCliOperator;
    }

    public int getNumObjects(final String type, final String namespace) {
        final String command = CmEditorCommandUtil.getAllMatchingMoOrPos(type, namespace);
        return executeCommandToGetFdns(command).size();
    }

    public String getMoFdn(final String moType, final String moId) {
        final String command = CmEditorCommandUtil.getMoForSpecificId(moType, moId);
        final Set<String> fdns = executeCommandToGetFdns(command);
        return fdns.isEmpty() ? EMPTY_STRING : fdns.iterator().next();
    }

    public Set<String> getMoFdnsWithMatchingId(final String moType, final String moId) {
        final String command = CmEditorCommandUtil.getMoForSpecificId(moType, moId);
        return executeCommandToGetFdns(command);
    }

    public Set<String> getMoFdns(final String moType, final String namespace) {
        final String command = CmEditorCommandUtil.getAllMatchingMoOrPos(moType, namespace);
        return executeCommandToGetFdns(command);
    }

    public Set<String> getMoFdns(final String filter, final String moType, final String namespace) {
        final String command = CmEditorCommandUtil.getAllMatchingMoOrPos(filter, moType, namespace);
        return executeCommandToGetFdns(command);
    }

    public Set<String> getMoFdnsFromSpecifiedNode(final String nodeName, final String moType) {
        final String command = CmEditorCommandUtil.getMoListFromNetworkElement(nodeName, moType);
        return executeCommandToGetFdns(command);
    }

    public Set<String> getMoFdnsFromSpecifiedNode(final String nodeName, final List<String> moTypes) {
        final String command = CmEditorCommandUtil.getMoListFromEnm(nodeName, moTypes);
        return executeCommandToGetFdns(command);
    }

    public Map<String, String> getAttributes(final String fdn, final String... attributeNames) {
        final String command = CmEditorCommandUtil.getAttributes(fdn, attributeNames);
        final EnmCliResponse response = executeRestCall(command);
        final Map<String, String> attributeNameValuePairs = response.getAttributesPerFdn()
                .get(fdn);
        final Map<String, String> result = new HashMap<>();
        for (final String key : attributeNameValuePairs.keySet()) {
            for (final String name : attributeNames) {
                if (key.equals(name)) {
                    result.put(name, attributeNameValuePairs.get(name));
                }
            }
        }
        return result;
    }

    public String getAttribute(final String fdn, final String attributeName) {
        final Map<String, String> result = getAttributes(fdn, attributeName);
        return result == null ? EMPTY_STRING : result.get(attributeName);
    }

    public String getAttribute(final String fdn, final String moIdAttributeName, final String attributeName) {
        final String command = CmEditorCommandUtil.getAttributes(fdn, moIdAttributeName, attributeName);
        final EnmCliResponse response = executeRestCall(command);
        final Map<String, String> resultMap = response.getAttributesPerFdn()
                .get(fdn);
        return resultMap.get(attributeName);
    }

    public String getAttribute(final String nodeName, final String moType, final String moIdAttributeValue, final String attributeName) {
        final String cmd = CmEditorCommandUtil.getAttribute(nodeName, moType, moIdAttributeValue, attributeName);
        final EnmCliResponse response = executeRestCall(cmd);
        final Map<String, Map<String, String>> attributesPerFdn = response.getAttributesPerFdn();
        return attributesPerFdn.values().iterator().next().get(attributeName);
    }

    public Map<String, String> getPersistentAttributes(final String fdn) {
        final String command = CmEditorCommandUtil.getFdn(fdn);
        final EnmCliResponse response = executeRestCall(command);
        return response.getAttributesPerFdn()
                .get(fdn);
    }

    public void markMoForDeletion(final String fdn) {
        deleteFdnList.add(fdn);
    }

    public void deleteMarkedMos() {
        final List<String> list = new LinkedList<String>(deleteFdnList);
        for (final String fdn : Lists.reverse(list)) {
            deleteManagedObject(fdn);
        }
        deleteFdnList.clear();
    }

    public EnmCliResponse deleteManagedObject(final String fdn) {
        final String command = CmEditorCommandUtil.deleteMo(fdn);
        return executeRestCall(command);
    }

    public String getRandomFdn(final String networkElementId, final String moType) {
        final Set<String> fdns = getMoFdnsFromSpecifiedNode(networkElementId, moType);
        return fdns.isEmpty() ? EMPTY_STRING : fdns.iterator().next();
    }

    public void deleteManagedObjects(final Set<String> fdns) {
        for (final String fdn : fdns) {
            deleteManagedObject(fdn);
        }
    }

    public Map<String, String> parseInputData(final String keys, final String values) {
        final String[] keysAsArray = keys.split(COMMA);
        final String[] valuesAsArray = values.split(COMMA);
        final Map<String, String> inputData = new HashMap<>();
        if (keysAsArray.length != valuesAsArray.length) {
            LOGGER.warn("Mismatch in input data you have requested to be parsed. Please check CSV file(s).");
        } else if (keysAsArray.length == 1 && valuesAsArray.length == 1) {
            inputData.put(keys, values);
        } else {
            for (int i = 0; i < keysAsArray.length; i++) {
                inputData.put(keysAsArray[i], valuesAsArray[i]);
            }
        }
        return inputData;
    }

    public String encloseInDoubleQuotes(final String string) {
        return DOUBLE_QUOTE.concat(string).concat(DOUBLE_QUOTE);
    }

    protected EnmCliResponse createManagedObject(final String fdn, final String attributes, final String model, final String version) {
        final String command = CmEditorCommandUtil.createMo(fdn, attributes, model, version);
        return executeRestCall(command);
    }

    protected EnmCliResponse createManagedObjectInheritedNamespace(final String fdn, final String attributes) {
        final String command = CmEditorCommandUtil.createMo(fdn, attributes);
        return executeRestCall(command);
    }

    protected EnmCliResponse executeRestCall(final String command) {
        checkHttpToolIsSet();
        LOGGER.info("Sending command to cmEditor: [ {} ]", command);
        final EnmCliResponse response = getEnmCliOperator().executeCliCommand(command, httpTool);
        LOGGER.info("Response received: [" + Joiner.on("\n")
                .join(response.getAllDtos()) + "]");
        return response;
    }

    protected void setHttpTool(final HttpTool httpTool) {
        isHttpToolSet = true;
        this.httpTool = httpTool;
    }

    protected void checkHttpToolIsSet() {
        if (!isHttpToolSet) {
            throw new HttpToolNotSetException();
        }
    }

    private Set<String> executeCommandToGetFdns(final String command) {
        final EnmCliResponse response = executeRestCall(command);
        LOGGER.debug(LOG_RESPONSE, response.toString());
        return response.getFdns();
    }
}
