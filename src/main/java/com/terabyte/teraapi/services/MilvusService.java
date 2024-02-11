package com.terabyte.teraapi.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
  
  public MilvusService() {
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  public MilvusDeviceResp loadDevicesByPage(Integer page) throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/dispositivos/listagem?total_registros=1000&order_by=id&is_descending=false&pagina=" + page;
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    MilvusDeviceResp data = mapper.readValue(body, MilvusDeviceResp.class);
    if (res.getBody() == null) {
      log.error("# Error: " + res.getStatusCode());
      return null;
    }
    log.info("> " + data.lista().size() + " devices loaded on page " + page);
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
    ResponseEntity<MilvusTicketResp> res = restTemplate.exchange(url, HttpMethod.POST, entity, MilvusTicketResp.class);
    return res.getBody();
  }

  public MilvusTicketResp loadNewRuntalentInTickets() throws IOException {
    // get current date minus 1 day
    Date date = new Date();
    date.setTime(date.getTime() - 1 * 24 * 60 * 60 * 1000);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = formatter.format(date);
    
    headers.set("Authorization", milvusKey);
    String payload = """
          {
            "filtro_body": {
              "data_hora_criacao_inicial": "%s 00:00:00",
              "data_hora_criacao_final": "%s 23:59:59",
              "assunto": "Formulario IN Runtalent",
              "cliente_id": 438713
            }
        }""";

    payload = String.format(payload, currentDate, currentDate);
    entity = new HttpEntity<String>(payload, headers);
    String url = baseUrl + "/chamado/listagem?is_descending=true&order_by=codigo&total_registros=100";
    ResponseEntity<MilvusTicketResp> res = restTemplate.exchange(url, HttpMethod.POST, entity, MilvusTicketResp.class);
    return res.getBody();
  }

  public void createTicket(HashMap<String, String> data) throws IOException {
    headers.set("Authorization", milvusKey);
    String payload = mapper.writeValueAsString(data);
    entity = new HttpEntity<String>(payload, headers);
    String url = baseUrl + "/chamado/criar";
    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
  }

  public void syncDevices() throws IOException {
    MilvusDeviceResp devices = new MilvusDeviceResp(new ArrayList<>(), null);
    try {
      devices = loadDevicesByPage(1);
      deviceRepository.batchUpsert(devices);
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
    Integer lastPage = devices.meta().paginate().last_page();
    if (lastPage > 1) {
      for (int i = 2; i <= lastPage; i++) {
        try {
          devices = loadDevicesByPage(i);
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
      clients = loadClients();
      log.info("> " + clients.size() + " clients loaded");
      clientRepository.batchUpsert(clients);
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
  }

  public void deleteOldDevices() {
    deviceRepository.deleteOldDevices();
  }
}