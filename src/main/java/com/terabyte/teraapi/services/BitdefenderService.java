package com.terabyte.teraapi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terabyte.teraapi.utils.BitCompaniesGroups;
import com.terabyte.teraapi.utils.BitEndpointList;
import com.terabyte.teraapi.utils.BitGroups;
import com.terabyte.teraapi.utils.BitNetworkGroups;
import com.terabyte.teraapi.utils.BitSecurityStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BitdefenderService {
  @Value("${bitdefender.key}")
  private String bitdefenderKey;
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
    BitNetworkGroups groups = getData(request, BitNetworkGroups.class);
    return groups.result();
  }

  public List<BitGroups> loadCompaniesGroups() throws JsonMappingException, JsonProcessingException {
    HashMap<String, Object> mapParams = new HashMap<>();
    mapParams.put("parentId", "55faa46e3a621503728b457a");
    String request = generateRequestString("getNetworkInventoryItems", mapParams);
    if (request == null) {
      log.error("Request for loadCompaniesGroups is null");
      return null;
    }
    BitCompaniesGroups groups = getData(request, BitCompaniesGroups.class);
    return groups.result().items();
  }

  @SuppressWarnings("null")
  public List<BitSecurityStatus> loadStatusByGroupId(@NonNull String groupId, @NonNull String groupName)
      throws JsonMappingException, JsonProcessingException {
    HashMap<String, Object> mapParams = new HashMap<>();
    mapParams.put("parentId", groupId);
    mapParams.put("perPage", 100);
    String request = generateRequestString("getEndpointsList", mapParams);
    BitEndpointList list = getData(request, BitEndpointList.class);
    if (list.result() == null)
      return new ArrayList<>();

    for (int i = 2; i <= list.result().pagesCount(); i++) {
      mapParams.put("page", i);
      request = generateRequestString("getEndpointsList", mapParams);
      if (request == null) {
        log.error("Request for loadStatusByGroupId is null");
        return null;
      }
      BitEndpointList newList = getData(request, BitEndpointList.class);
      list.result().items().addAll(newList.result().items());
      mapParams.remove("page");
    }
    return list.result().items();
  }

  public String generateRequestString(String method, HashMap<String, Object> mapParams) {
    String params = "{}";
    try {
      params = mapper.writeValueAsString(mapParams);
    } catch (Exception e) {
      log.error("Error while parsing params to json: " + e.getMessage());
    }
    String request = "{\"params\": " + params + ",\"jsonrpc\": \"2.0\",\"method\": \"" + method + "\",\"id\": \"1\"}";
    return request;
  }

  private <T> T getData(@NonNull String request, @NonNull Class<T> entityClass) {
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
        .toEntity(entityClass)
        .block()
        .getBody();
  }
}
