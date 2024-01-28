package com.terabyte.teraapi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terabyte.teraapi.models.SecurityStatus;
import com.terabyte.teraapi.repositories.DeviceRepository;
import com.terabyte.teraapi.repositories.SecurityStatusRepository;
import com.terabyte.teraapi.utils.BitCompaniesGroups;
import com.terabyte.teraapi.utils.BitEndpointList;
import com.terabyte.teraapi.utils.BitGroups;
import com.terabyte.teraapi.utils.BitNetworkGroups;

@Service
public class BitdefenderService {
  @Value("${bitdefender.key}")
  private String bitdefenderKey;
  @Autowired
  private SecurityStatusRepository statusRepository = new SecurityStatusRepository();
  @Autowired
  private DeviceRepository deviceRepository = new DeviceRepository();

  private Logger log = LoggerFactory.getLogger("BitdefenderService");

  private final String url = "https://cloud.gravityzone.bitdefender.com/api/v1.0/jsonrpc/network";
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private String rootGroupId = "55faa46e3a621503728b457c";

  public List<BitGroups> loadNetworkGroups() throws JsonMappingException, JsonProcessingException {
    HashMap<String, Object> mapParams = new HashMap<>();
    mapParams.put("parentId", rootGroupId);
    String request = generateRequestString("getCustomGroupsList", mapParams);
    if (request == null) {
      log.error("Request for loadNetworkGroups is null");
      return new ArrayList<>();
    }
    ResponseEntity<String> response = getResponse(request);
    BitNetworkGroups res = mapper.readValue(response.getBody(), BitNetworkGroups.class);
    return res.result();
  }

  public List<BitGroups> loadCompaniesGroups() throws JsonMappingException, JsonProcessingException {
    HashMap<String, Object> mapParams = new HashMap<>();
    mapParams.put("parentId", "55faa46e3a621503728b457a");
    String request = generateRequestString("getNetworkInventoryItems", mapParams);
    if (request == null) {
      log.error("Request for loadCompaniesGroups is null");
      return null;
    }
    ResponseEntity<String> response = getResponse(request);
    BitCompaniesGroups res = mapper.readValue(response.getBody(), BitCompaniesGroups.class);
    return res.result().items();
  }

  public List<SecurityStatus> loadStatusByGroupId(@NonNull String groupId, @NonNull String groupName)
      throws JsonMappingException, JsonProcessingException {
    HashMap<String, Object> mapParams = new HashMap<>();
    mapParams.put("parentId", groupId);
    mapParams.put("perPage", 100);
    String request = generateRequestString("getEndpointsList", mapParams);
    if (request == null) {
      log.error("Request for loadStatusByGroupId is null");
      return null;
    }
    ResponseEntity<String> response = getResponse(request);
    BitEndpointList res = mapper.readValue(response.getBody(), BitEndpointList.class);
    if (res.result() == null)
      return new ArrayList<>();

    if (res.result().pagesCount() > 1) {
      for (int i = 2; i <= res.result().pagesCount(); i++) {
        mapParams.put("page", i);
        request = generateRequestString("getEndpointsList", mapParams);
        if (request == null) {
          log.error("Request for loadStatusByGroupId is null");
          return null;
        }
        response = getResponse(request);
        BitEndpointList res2 = mapper.readValue(response.getBody(), BitEndpointList.class);
        res.result().items().addAll(res2.result().items());
        mapParams.remove("page");
      }
    }
    return res.mapToSecurityStatus(groupName, deviceRepository);
  }

  public void syncSecurityStatus() throws JsonMappingException, JsonProcessingException {
    List<BitGroups> groups = loadNetworkGroups();
    upsertGroupsStatus(groups);
    groups = loadCompaniesGroups();
    upsertGroupsStatus(groups);
  }

  public String generateRequestString(String method, HashMap<String, Object> mapParams) {
    String params = "{}";
    try {
      if (mapParams.size() > 0) {
        params = mapper.writeValueAsString(mapParams);
      }
    } catch (Exception e) {
      log.error("Error while parsing params to json: " + e.getMessage());
    }
    String request = "{\"params\": " + params + ",\"jsonrpc\": \"2.0\",\"method\": \"" + method + "\",\"id\": \"1\"}";
    return request;
  }

  private ResponseEntity<String> getResponse(@NonNull String request) {
    String loginString = bitdefenderKey + ":";
    String encodedUserPassSequence = new String(java.util.Base64.getEncoder().encode(loginString.getBytes()));
    String authHeader = "Basic " + encodedUserPassSequence;
    WebClient webClient = WebClient.builder().baseUrl(url).build();
    return webClient.post()
        .uri("")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, authHeader)
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .toEntity(String.class)
        .block();
  }

  private void upsertGroupsStatus(@NonNull List<BitGroups> groups)
      throws JsonMappingException, JsonProcessingException {
    for (BitGroups group : groups) {
      if (group.id() == null || group.name().isEmpty()) {
        log.error("Group id or name is empty");
        continue;
      }
      List<SecurityStatus> statuses = loadStatusByGroupId(group.id(), group.name());
      log.info("> Load " + group.name() + " - " + statuses.size() + " statuses");
      statusRepository.batchUpsert(statuses);
    }
  }
}
