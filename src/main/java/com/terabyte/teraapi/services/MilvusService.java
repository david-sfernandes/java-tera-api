package com.terabyte.teraapi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.repositories.DeviceRepository;
import com.terabyte.teraapi.utils.MilvusClientResp;
import com.terabyte.teraapi.utils.MilvusDeviceResp;
import com.terabyte.teraapi.utils.tickets.MilvusTicketResp;

@Service
public class MilvusService {
  @Value("${milvus.key}")
  private String milvusKey;
  @Autowired
  private ClientRepository clientRepository = new ClientRepository();
  @Autowired
  private DeviceRepository deviceRepository = new DeviceRepository();
  private final String baseUrl = "https://apiintegracao.milvus.com.br/api";
  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private HttpHeaders headers = new HttpHeaders();
  private HttpEntity<String> entity = new HttpEntity<String>(headers);

  private Logger log = LoggerFactory.getLogger("MilvusService");

  public MilvusService() {
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  public MilvusDeviceResp loadDevices() throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/dispositivos/listagem?total_registros=1000&order_by=id&is_descending=false&pagina=1";
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    MilvusDeviceResp data = mapper.readValue(body, MilvusDeviceResp.class);
    log.info("> " + data.lista().size() + " devices loaded");
    return data;
  }

  public MilvusDeviceResp loadDevicesByPage(Integer page) throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/dispositivos/listagem?total_registros=1000&order_by=id&is_descending=false&pagina=" + page;
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    MilvusDeviceResp data = mapper.readValue(body, MilvusDeviceResp.class);
    log.info("> " + data.lista().size() + " devices loaded");
    return data;
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

  public MilvusTicketResp loadTickets() throws IOException {
    MilvusTicketResp tickets = loadTicketsByPage("1");
    if (tickets.meta().paginate().last_page() > 1) {
      for (Integer i = 2; i <= tickets.meta().paginate().last_page(); i++) {
        MilvusTicketResp pageData = loadTicketsByPage(i.toString());
        tickets.lista().addAll(pageData.lista());
      }
    }
    return tickets;
  }

  public MilvusTicketResp loadTicketsByPage(String page) throws IOException {
    headers.set("Authorization", milvusKey);
    String payload = """
          {
            "filtro_body": {
              "data_hora_criacao_inicial": "2023-12-31 23:59:59",
              "data_hora_criacao_final": "2024-01-31 23:59:59"
          }
        }""";
    entity = new HttpEntity<String>(payload, headers);
    String url = baseUrl + "/chamado/listagem?is_descending=true&order_by=codigo&total_registros=10&pagina=" + page;
    ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    String body = res.getBody();
    MilvusTicketResp data = mapper.readValue(body, MilvusTicketResp.class);
    return data;
  }

  public void syncDevices() throws IOException {
    MilvusDeviceResp devices = new MilvusDeviceResp(new ArrayList<>(), null);
    try {
      devices = loadDevices();
      deviceRepository.batchUpsert(devices.mapToDevices(clientRepository));
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
    Integer lastPage = devices.meta().paginate().last_page();
    if (lastPage > 1) {
      for (int i = 2; i <= lastPage; i++) {
        try {
          devices = loadDevicesByPage(i);
          deviceRepository.batchUpsert(devices.mapToDevices(clientRepository));
        } catch (Exception e) {
          log.error("# Error: " + e.getMessage());
        }
      }
    }
  }

  public void syncClients() throws IOException {
    List<Client> clients = new ArrayList<>();
    try {
      clients = loadClients();
      log.info("> " + clients.size() + " clients loaded");
      clientRepository.batchUpsert(clients);
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
  }
}