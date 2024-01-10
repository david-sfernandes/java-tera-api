package com.terabyte.teraapi.services;

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
import com.terabyte.teraapi.repositories.SecurityStatusRepository;
import com.terabyte.teraapi.utils.BitGroupsResp;

@Service
public class BitdefenderService {
  @Value("${bitdefender.key}")
  private String bitdefenderKey;
  @Autowired
  private SecurityStatusRepository statusRepository = new SecurityStatusRepository();
  private final String baseUrl = "https://cloud.gravityzone.bitdefender.com/api/v1.0/jsonrpc/network";
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private String rootGroupId = "55faa46e3a621503728b457c";

  public BitGroupsResp loadGroups() throws JsonMappingException, JsonProcessingException {
    String request = generateRequestString("getCustomGroupsList", rootGroupId);
    ResponseEntity<String> result = getResponse(request);
    BitGroupsResp res = mapper.readValue(result.getBody(), BitGroupsResp.class);
    return res;
  }

  public String loadStatusByGroupId(String groupId) {
    String request = generateRequestString("getEndpointsList", groupId);
    ResponseEntity<String> result = getResponse(request);
    return result.getBody();
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
    WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
    return webClient.post()
        .uri("")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, authHeader)
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .toEntity(String.class)
        .block();
  }
}
