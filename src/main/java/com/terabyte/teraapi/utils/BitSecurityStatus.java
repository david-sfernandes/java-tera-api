package com.terabyte.teraapi.utils;

import java.util.List;

public record BitSecurityStatus(String id, String name, String label, String fqdn, String groupId,
    Boolean isManaged, Integer machineType, String operatingSystemVersion, String ip, List<String> macs,
    String ssid, Boolean managedWithBest, Boolean managedRelay) {

}
