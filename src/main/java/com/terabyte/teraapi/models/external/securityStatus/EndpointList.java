package com.terabyte.teraapi.models.external.securityStatus;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.DeviceRepository;

public record EndpointList(EndpointListResults result) {

  public List<SecurityStatus> mapToSecurityStatus(String group, DeviceRepository deviceRepository) {
    if (result.items() == null)
      return new ArrayList<>();
      
    Instant instant = Instant.now();
    Date lastSync = new Date(instant.toEpochMilli());
    return result.items().stream().map((item) -> {
      List<Integer> devicesId = deviceRepository.getIdSecurityStatus(item.macs().get(0), item.name());
      Integer deviceId = devicesId.size() > 0 ? devicesId.get(0) : null;

      Boolean isManagedWithBest = item.managedWithBest() == null ? false : item.managedWithBest();
      isManagedWithBest = item.managedRelay() == null ? isManagedWithBest : item.managedRelay();

      return SecurityStatus
          .builder()
          .id(item.id())
          .name(item.name())
          .mac(item.macs().get(0))
          .group(group)
          .lastSync(lastSync)
          .isManaged(item.isManaged())
          .isManagedWithBest(isManagedWithBest)
          .deviceId(deviceId)
          .build();
    }).collect(Collectors.toList());
  }

}
