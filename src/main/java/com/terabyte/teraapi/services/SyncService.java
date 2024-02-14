package com.terabyte.teraapi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.repositories.DeviceRepository;
import com.terabyte.teraapi.repositories.SecurityStatusRepository;
import com.terabyte.teraapi.utils.BitGroups;
import com.terabyte.teraapi.utils.MilvusDeviceResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SyncService {
  @Autowired
  private final MilvusService milvusService = new MilvusService();
  @Autowired
  private final BitdefenderService bitdefenderService = new BitdefenderService();
  @Autowired
  private final ClientRepository clientRepository = new ClientRepository();
  @Autowired
  private final DeviceRepository deviceRepository = new DeviceRepository();
  @Autowired
  private final SecurityStatusRepository statusRepository = new SecurityStatusRepository();

  public void syncDevices() throws IOException {
    MilvusDeviceResp devices = new MilvusDeviceResp(new ArrayList<>(), null);
    try {
      devices = milvusService.loadDevicesByPage(1);
      deviceRepository.batchUpsert(devices);
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
    Integer lastPage = devices.meta().paginate().last_page();
    if (lastPage > 1) {
      for (int i = 2; i <= lastPage; i++) {
        try {
          devices = milvusService.loadDevicesByPage(i);
          deviceRepository.batchUpsert(devices);
        } catch (Exception e) {
          log.error("# Error: " + e.getMessage());
        }
      }
    }
  }

  public void syncClients() throws IOException {
    List<Client> clients = new ArrayList<>();
    try {
      clients = milvusService.loadClients();
      log.info("> " + clients.size() + " clients loaded");
      clientRepository.batchUpsert(clients);
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
  }

  public void syncSecurityStatus() throws JsonMappingException, JsonProcessingException {
    List<BitGroups> groups = bitdefenderService.loadNetworkGroups();
    if (groups != null) {
      upsertGroupsStatus(groups);
    }
    groups = bitdefenderService.loadCompaniesGroups();
    if (groups != null) {
      upsertGroupsStatus(groups);
    }
  }

  public void deleteOldDevices() {
    deviceRepository.deleteOldDevices();
  }

  @SuppressWarnings("null")
  private void upsertGroupsStatus(@NonNull List<BitGroups> groups)
      throws JsonMappingException, JsonProcessingException {
    for (BitGroups group : groups) {
      if (group.id() == null || group.name().isEmpty()) {
        log.error("Group id or name is empty");
        continue;
      }
      List<SecurityStatus> statuses = bitdefenderService.loadStatusByGroupId(group.id(), group.name());
      log.info("> Load " + group.name() + " - " + statuses.size() + " statuses");
      statusRepository.batchUpsert(statuses);
    }
  }

  public void syncAllData() {
    try {
      syncClients();
      syncDevices();
      deleteOldDevices();
      syncSecurityStatus();
    } catch (Exception e) {
      log.error("Error on sync all data", e);
    }
  }
}
