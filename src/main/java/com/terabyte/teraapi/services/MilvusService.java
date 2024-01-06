package com.terabyte.teraapi.services;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terabyte.teraapi.utils.MilvusDeviceResp;

@Service
public class MilvusService {
  private final String baseUrl = "https://apiintegracao.milvus.com.br/api";
  // get the value from file env.yml
  @Value("${milvus.key}")
  private String milvusKey;

  public MilvusDeviceResp loadDevices() throws IOException {
    String url = baseUrl + "/dispositivos/listagem?total_registros=5&order_by=id&is_descending=false&pagina=1";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", milvusKey);
    HttpEntity<String> entity = new HttpEntity<String>(headers);
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MilvusDeviceResp data = mapper.readValue(body, MilvusDeviceResp.class);
    return data;
  }
}