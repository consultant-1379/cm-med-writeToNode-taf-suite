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

package com.ericsson.oss.mediation.cm.taf.constants;

import java.util.regex.Pattern;

public abstract class Constants {
    public static final String SPACE = " ";
    public static final String EMPTY_STRING = "";
    public static final String COMMA = ",";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String COLON = ":";
    public static final String SEMI_COLON = ";";
    public static final String WILDCARD = "*";
    public static final String DOT = ".";
    public static final String LEFT_ROUND_BRACKET = "(";
    public static final String RIGHT_ROUND_BRACKET = ")";
    public static final String EQUALS = "=";
    public static final String DOUBLE_EQUALS = "==";
    public static final String NULL = "null";
    public static final String ID = "Id";
    public static final String OSS_TOP_MODEL_NS = "OSS_TOP";
    public static final String OSS_TOP_MODEL_VERSION = "3.0.0";
    public static final String OSS_NE_DEF_MODEL_NS = "OSS_NE_DEF";
    public static final String OSS_NE_DEF_MODEL_VERSION = "2.0.0";
    public static final String OSS_NE_CM_DEF_MODEL_NS = "OSS_NE_CM_DEF";
    public static final String OSS_NE_CM_DEF_MODEL_VERSION = "1.0.0";
    public static final String SUBNETWORK = "SubNetwork";
    public static final String SUBNETWORKID = "SubNetworkId";
    public static final String MECONTEXT = "MeContext";
    public static final String MECONTEXTID = "MeContextId";
    public static final String NETWORKELEMENT = "NetworkElement";
    public static final String NETWORKELEMENTID = "networkElementId";
    public static final String MANAGED_ELEMENT = "ManagedElement";
    public static final String MANAGED_ELEMENT_ID = "managedElementId";
    public static final String INVENTORY_SUPERVISION = "InventorySupervision";
    public static final String FM_ALARM_SUPERVISION = "FmAlarmSupervision";
    public static final String CM_NODE_HEARTBEAT_SUPERVISION = "CmNodeHeartbeatSupervision";
    public static final String OSSPREFIX = "ossPrefix";
    public static final String OSSMODELIDENTITY = "ossModelIdentity";
    public static final String MIMINFO = "mimInfo";
    public static final String NETYPE = "neType";
    public static final String PLATFORMTYPE = "platformType";
    public static final String NEPRODUCTVERSION = "neProductVersion";
    public static final String IPADDRESS = "ipAddress";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TRANSPORT_PROTOCOL = "transportProtocol";
    public static final String SNMP_VERSION = "snmpVersion";
    public static final String SNMPAGENTPORT = "snmpAgentPort";
    public static final String SNMPWRITECOMMUNITY = "snmpWriteCommunity";
    public static final String SNMPREADCOMMUNITY = "snmpReadCommunity";
    public static final String SNMPSECURITYNAME = "snmpSecurityName";
    public static final String SNMPSECURITYLEVEL = "snmpSecurityLevel";
    public static final String STRICTHOSTKEYCHECKING = "strictHostKeyChecking";
    public static final String CMFUNCTION = "CmFunction";
    public static final String TRANSPORTNETWORK = "TransportNetwork";
    public static final String SCTP = "Sctp";
    public static final String USERLABEL = "userLabel";
    public static final String GENERATIONCOUNTER = "generationCounter";
    public static final String DELETE_NRM_DATA_FROM_ENM = "deleteNrmDataFromEnm";
    public static final String CMEDIT_GET = "cmedit get ";
    public static final String CMEDIT_SET = "cmedit set ";
    public static final String CMEDIT_ACTION = "cmedit action ";
    public static final String CMEDIT_CREATE = "cmedit create ";
    public static final String CMEDIT_DELETE = "cmedit delete ";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ACTIVE_STATE_TRUE = "active=true";
    public static final String ACTIVE_STATE_FALSE = "active=false";
    public static final String SYNC_STATUS_SYNCED = "syncStatus=SYNCHRONIZED";
    public static final String NAMESPACE_TAG = "-ns=";
    public static final String VERSION_TAG = "-v=";
    public static final String ALL_TAG = "-ALL";
    public static final String CMEDIT_RSP_FOR_ONE_MO = "1 instance(s)";
    public static final String CMEDIT_RSP_FOR_ZERO_MO = "0 instance(s)";
    public static final String CMEDIT_COMMON_RSP = " instance(s)";
    public static final String CMEDIT_MO_DELETE_RSP = "instance(s) deleted";
    public static final String CMEDIT_SYNC_OK_RESPONSE = "\"syncStatus : SYNCHRONIZED\"";
    private static final String COMMON_REGEX = "([a-zA-Z0-9]+=[a-zA-Z0-9]+)";
    public static final Pattern COMMON_PATTERN = Pattern.compile("([a-zA-Z0-9]+=[a-zA-Z0-9]+)");
    private static final String PRIMITIVE_REGEX = "([a-zA-Z0-9]+)=([a-zA-Z0-9-]+)";
    public static final Pattern PRIMITIVE_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=([a-zA-Z0-9-]+)");
    private static final String LIST_OF_PRIMITIVE_REGEX = "([a-zA-Z0-9]+)=\\[([a-zA-Z0-9,-]+)\\]";
    public static final Pattern LIST_OF_PRIMITIVE_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=\\[([a-zA-Z0-9,-]+)\\]");
    public static final String LIST_OF_STRUCT_REGEX = "([a-zA-Z0-9]+)=(\\[\\(.*?\\)\\])";
    public static final Pattern LIST_OF_STRUCT_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=(\\[\\(.*?\\)\\])");
    public static final String STRUCT_REGEX = "([a-zA-Z0-9]+)=(\\(.*?\\))";
    public static final Pattern STRUCT_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=(\\(.*?\\))");
    private static final String STRUCT_LIST_REGEX = "([a-zA-Z0-9]+=\\[[a-zA-Z0-9,-]+)\\]";
    public static final Pattern STRUCT_LIST_PATTERN = Pattern.compile("([a-zA-Z0-9]+=\\[[a-zA-Z0-9,-]+)\\]");
    private static final String NETSIM_SIMPLE_REGEX = "([a-zA-Z0-9]+)=([a-zA-Z0-9,-]+)";
    public static final Pattern NETSIM_SIMPLE_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=([a-zA-Z0-9,-]+)");
    private static final String NETSIM_LIST_OF_STRUCT_REGEX = "([a-zA-Z0-9]+)=(\\[\\{[a-zA-z0-9\\[\\]\\{\\}\\s\\:\",-.]+)\\]";
    public static final Pattern NETSIM_LIST_OF_STRUCT_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=(\\[\\{[a-zA-z0-9\\[\\]\\{\\}\\s\\:\",-.]+)\\]");
    private static final String NETSIM_STRUCT_FINDER_REGEX = "([a-zA-Z0-9]+)=,";
    public static final Pattern NETSIM_STRUCT_PATTERN = Pattern.compile("([a-zA-Z0-9]+)=,");
    public static final String COMMA_OUTSIDE_QUOTES_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String EQUALS_OUTSIDE_QUOTES_REGEX = "=(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String HTTP_REGEX = "\\d+ instance";
    public static final String CONFIG_FLAG = " -c=";
    public static final String LIVE_CONFIG = "Live";
    public static final String LOG_RESPONSE = "Response received from cmEditor: [ {} ]";
    public static final String LICENSE_FILE_NAME = "data/licenseFile.txt";
}
