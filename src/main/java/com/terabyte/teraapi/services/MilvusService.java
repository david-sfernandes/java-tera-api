package com.terabyte.teraapi.services;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.Device;
import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.utils.MilvusClientResp;
import com.terabyte.teraapi.utils.MilvusDeviceResp;

@Service
public class MilvusService {
  @Value("${milvus.key}")
  private String milvusKey;
  @Autowired
  private ClientRepository clientRepository = new ClientRepository();
  private final String baseUrl = "https://apiintegracao.milvus.com.br/api";
  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private HttpHeaders headers = new HttpHeaders();
  private HttpEntity<String> entity = new HttpEntity<String>(headers);

  public MilvusService() {
    headers.setContentType(MediaType.APPLICATION_JSON);
  }
  
  public List<Device> loadDevices() throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/dispositivos/listagem?total_registros=5&order_by=id&is_descending=false&pagina=1";
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    MilvusDeviceResp data = mapper.readValue(body, MilvusDeviceResp.class);
    return data.mapToDevices(clientRepository);
  }

  public List<Client> loadClients() throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/cliente/busca";
    ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    String body = res.getBody();
    MilvusClientResp data = mapper.readValue(body, MilvusClientResp.class);
    return data.mapToClient();
  }
}