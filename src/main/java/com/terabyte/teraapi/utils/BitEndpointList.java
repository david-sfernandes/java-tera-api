package com.terabyte.teraapi.utils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.DeviceRepository;

public record BitEndpointList(BitEndpointListResults result) {
  public List<SecurityStatus> mapToSecurityStatus(String group, DeviceRepository deviceRepository) {
    String lastSync = Date.from(new Date().toInstant()).toString();
    return result.items().stream().map((item) -> {
      List<Integer> devicesId = deviceRepository.getIdSecurityStatus(item.macs().get(0), item.name());
      Integer deviceId = devicesId.size() > 0 ? devicesId.get(0) : null;
      
      return SecurityStatus
        .builder()
        .id(item.id())
        .name(item.name())
        .mac(item.macs().get(0))
        .group(group)
        .lastSync(lastSync)
        .isManaged(item.isManaged())
        .isManagedWithBest(item.managedWithBest())
        .deviceId(deviceId)
        .build();
    }).collect(Collectors.toList());
  }

}
