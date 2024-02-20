package com.terabyte.teraapi.models.external.securityStatus;

import java.util.List;

public record BitSecurityStatus(String id, String name, String label, String fqdn, String groupId,
    Boolean isManaged, Integer machineType, String operatingSystemVersion, String ip, List<String> macs,
    String ssid, Boolean managedWithBest, Boolean managedRelay) {

}
