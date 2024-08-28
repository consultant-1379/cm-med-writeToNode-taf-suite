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
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.COMMA_OUTSIDE_QUOTES_REGEX;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.CONFIG_FLAG;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.EQUALS;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.EQUALS_OUTSIDE_QUOTES_REGEX;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.ID;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.LIVE_CONFIG;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.MECONTEXT;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.NULL;
import static com.ericsson.oss.mediation.cm.taf.constants.Constants.OSS_TOP_MODEL_NS;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.oss.services.scriptengine.spi.dtos.AbstractDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;
import com.ericsson.oss.testware.enm.cli.EnmCliResponseImpl;
import com.ericsson.oss.testware.nodeintegration.utilities.CmEditorCommandUtil;
import com.google.common.collect.Lists;

@Operator
public class WriteNodeOperator extends BaseOperator {

    @Override
    public void setHttpTool(final HttpTool tool) {
        super.setHttpTool(tool);
        isHttpToolSet = true;
    }

    public EnmCliResponse createManagedObject(final String fdn, final String attributes) {
        final StringBuilder sb = buildCreateMoAttributes(fdn, attributes);
        return createManagedObjectInheritedNamespace(fdn, sb.toString());
    }

    public EnmCliResponse createNonLiveManagedObject(final String moFdn, final String attributes, final String config) {
        if (config == null || config.equalsIgnoreCase(LIVE_CONFIG)) {
            throw new IllegalArgumentException("Non-live config must be specified, but was " + config);
        }
        final StringBuilder sb = buildCreateMoAttributes(moFdn, attributes).append(CONFIG_FLAG + config);
        return createManagedObjectInheritedNamespace(moFdn, sb.toString());
    }

    public EnmCliResponse updateManagedObject(final String fdn, final String attributes) {
        final String command = CmEditorCommandUtil.updateMO(fdn, attributes);
        return executeRestCall(command);
    }

    public EnmCliResponse updateManagedObject(final String fdn, final String attributeName, final String attributeValue) {
        return updateManagedObject(fdn, attributeName + EQUALS + attributeValue);
    }

    public EnmCliResponse updateManagedObjects(final String filter, final String moType, final String attributes) {
        final String command = CmEditorCommandUtil.updateMultipleMos(filter, moType, attributes);
        return executeRestCall(command);
    }

    public EnmCliResponse getManagedObjects(final String filter, final String moType, final String attribute) {
        final String command = CmEditorCommandUtil.getMosWithMatchingAttributeValue(filter, moType, attribute);
        return executeRestCall(command);
    }

    public EnmCliResponse executeActionOnMo(final String fdn, final String action, final String attributes) {
        final String command = CmEditorCommandUtil.performAction(fdn, action, parseAttributes(attributes));
        return executeRestCall(command);
    }

    public EnmCliResponse executeActionOnMo(final String filter, final String action, final String moType, final String attributes) {
        return executeRestCall(CmEditorCommandUtil.performAction(moType, filter, action, parseAttributes(attributes)));
    }

    public EnmCliResponse createSingleMoOnMultipleNodes(final String filter, final String ldn, final String attributes) {
        final List<AbstractDto> result = Lists.newArrayList();
        final Set<String> meContextFdns = getMoFdns(filter, MECONTEXT, OSS_TOP_MODEL_NS);
        for (final String fdn : meContextFdns) {
            final String resultingFdn = fdn.concat(COMMA)
                    .concat(ldn);
            result.addAll(createManagedObject(resultingFdn, attributes).getAllDtos());
        }
        return new EnmCliResponseImpl(new ResponseDto(result));
    }

    public EnmCliResponse deleteSingleMoOnMultipleNodes(final String filter, final String ldn) {
        final List<AbstractDto> result = Lists.newArrayList();
        final Set<String> meContextFdns = getMoFdns(filter, MECONTEXT, OSS_TOP_MODEL_NS);
        for (final String fdn : meContextFdns) {
            final String resultingFdn = fdn.concat(COMMA)
                    .concat(ldn);
            result.addAll(deleteManagedObject(resultingFdn).getAllDtos());
        }
        return new EnmCliResponseImpl(new ResponseDto(result));
    }

    public EnmCliResponse deleteMatchingMosFromMultipleNodes(final String filter, final String moType) {
        return executeRestCall(CmEditorCommandUtil.deleteMos(filter, moType));
    }

    public List<String> convertTypesToFdns(final String nodeName, final String types) {
        final List<String> fdns = new LinkedList<String>();
        String fdn;
        for (final String type : types.split(COMMA)) {
            fdn = getMoFdnsFromSpecifiedNode(nodeName, type).iterator()
                    .next();
            fdns.add(fdn);
        }
        return fdns;
    }

    public EnmCliResponse getAttributes(final String filter, final String moType, final String[] attributeNames) {
        final String cmCommand = CmEditorCommandUtil.getAttributes(filter, moType, attributeNames);
        return executeRestCall(cmCommand);
    }

    public Set<String> createMaxChildren(final int cardinality, final String parentFdn, final String childMoType, final String moAttributes) {
        final Set<String> fdns = new HashSet<String>();
        final int numberOfExistingChildren = getNumberMosWithSpecificParent(parentFdn, childMoType);
        if (numberOfExistingChildren < cardinality) {
            final int mosToCreate = cardinality - numberOfExistingChildren;
            for (int i = 1; i <= mosToCreate; i++) {
                final String fdn = parentFdn + "," + childMoType + "=test" + i;
                createManagedObject(fdn, moAttributes);
                fdns.add(fdn);
            }
        }
        return fdns;
    }

    public EnmCliResponse deleteNonLiveManagedObject(final String fdn, final String config) {
        if (config == null || config.equalsIgnoreCase(LIVE_CONFIG)) {
            throw new IllegalArgumentException("Non-live config must be specified, but was " + config);
        }
        final String command = CmEditorCommandUtil.deleteMo(fdn + CONFIG_FLAG + config);
        return executeRestCall(command);
    }

    private StringBuilder buildCreateMoAttributes(final String fdn, final String attributes) {
        final String rdn = getRdnFromFdn(fdn);
        final StringBuilder sb = new StringBuilder();
        if (attributes != null && !attributes.isEmpty()) {
            if (!checkForIdAttribute(rdn, attributes)) {
                sb.append(getIdAttribute(rdn));
                sb.append(COMMA);
            }
            sb.append(attributes);
        } else {
            sb.append(getIdAttribute(rdn));
        }
        return sb;
    }

    private int getNumberMosWithSpecificParent(final String parentFdn, final String childMoType) {
        final String networkElementId = CmEditorCommandUtil.getNodeName(parentFdn);
        final Set<String> nodeMoFdns = getMoFdnsFromSpecifiedNode(networkElementId, childMoType);
        int numberOfExistingChildren = 0;
        for (final String mo : nodeMoFdns) {
            if (mo.contains(parentFdn)) {
                numberOfExistingChildren++;
            }
        }
        return numberOfExistingChildren;
    }

    private Map<String, String> parseAttributes(final String parametersAsString) {
        final Map<String, String> parameters = new LinkedHashMap<String, String>();
        if (!parametersAsString.equalsIgnoreCase(NULL)) {
            final String[] parsedAttrList = parametersAsString.split(COMMA_OUTSIDE_QUOTES_REGEX);
            for (final String attr : parsedAttrList) {
                final String[] tmp = attr.split(EQUALS_OUTSIDE_QUOTES_REGEX);
                final String key = tmp[0];
                final String value = tmp[1];
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    private String getRdnFromFdn(final String fdn) {
        return fdn.substring(fdn.lastIndexOf(COMMA) + 1);
    }

    private boolean checkForIdAttribute(final String rdn, final String attributes) {
        final String moType = rdn.split(EQUALS)[0];
        return StringUtils.containsIgnoreCase(attributes, moType + ID);
    }

    private String getIdAttribute(final String mo) {
        final String[] splitMo = mo.split(EQUALS);
        final StringBuilder sb = new StringBuilder(splitMo[0]);
        sb.append(ID);
        sb.append(EQUALS);
        sb.append(splitMo[1]);
        return sb.toString();
    }

}
