package com.terabyte.teraapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  private final String url = "https://cloud.gravityzone.bitdefender.com/api/v1.0/jsonrpc/network";
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private String rootGroupId = "55faa46e3a621503728b457c";

  public List<BitGroups> loadNetworkGroups() throws JsonMappingException, JsonProcessingException {
    String request = generateRequestString("getCustomGroupsList", rootGroupId);
    ResponseEntity<String> response = getResponse(request);
    BitNetworkGroups res = mapper.readValue(response.getBody(), BitNetworkGroups.class);
    return res.result();
  }

  public List<BitGroups> loadCompaniesGroups() throws JsonMappingException, JsonProcessingException {
    String request = generateRequestString("getNetworkInventoryItems", "55faa46e3a621503728b457a");
    ResponseEntity<String> response = getResponse(request);
    BitCompaniesGroups res = mapper.readValue(response.getBody(), BitCompaniesGroups.class);
    return res.result().items();
  }

  public List<SecurityStatus> loadStatusByGroupId(String groupId) throws JsonMappingException, JsonProcessingException {
    String request = generateRequestString("getEndpointsList", groupId);
    ResponseEntity<String> response = getResponse(request);
    BitEndpointList res = mapper.readValue(response.getBody(), BitEndpointList.class);
    return res.mapToSecurityStatus(groupId, deviceRepository);
  }

  public void syncSecurityStatus() throws JsonMappingException, JsonProcessingException {
    List<BitGroups> groups = loadNetworkGroups();
    upsertGroupsStatus(groups);
    groups = loadCompaniesGroups();
    upsertGroupsStatus(groups);
  }

  private String generateRequestString(String method, String parentId) {
    String params = (parentId == null) ? "{}" : "{\"parentId\": \"" + parentId + "\"}";
    String request = "{\"params\": " + params + ",\"jsonrpc\": \"2.0\",\"method\": \"" + method + "\",\"id\": \"1\"}";
    return request;
  }

  private ResponseEntity<String> getResponse(String request) {
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

  private void upsertGroupsStatus(List<BitGroups> groups) throws JsonMappingException, JsonProcessingException {
    for (BitGroups group : groups) {
      List<SecurityStatus> statuses = loadStatusByGroupId(group.id());
      System.out.println("> Load " + group.name() + " - " + statuses.size() + " statuses");
      statusRepository.batchUpsert(statuses);
    }
  }
}
