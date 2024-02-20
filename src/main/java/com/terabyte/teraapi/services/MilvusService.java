package com.terabyte.teraapi.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.terabyte.teraapi.models.external.clients.ClientResp;
import com.terabyte.teraapi.models.external.devices.DeviceResp;
import com.terabyte.teraapi.models.external.tickets.TicketResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MilvusService {
  @Value("${milvus.key}")
  private String milvusKey;
  private final String baseUrl = "https://apiintegracao.milvus.com.br/api";
  private RestTemplate restTemplate = new RestTemplate();
  private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private HttpHeaders headers = new HttpHeaders();
  @SuppressWarnings("null")
  private HttpEntity<String> entity = new HttpEntity<String>(headers);

  public MilvusService() {
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  public DeviceResp loadDevicesByPage(Integer page) throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/dispositivos/listagem?total_registros=1000&order_by=id&is_descending=false&pagina=" + page;
    ResponseEntity<String> res = restTemplate.postForEntity(url, entity, String.class);
    String body = res.getBody();
    DeviceResp data = mapper.readValue(body, DeviceResp.class);
    if (res.getBody() == null) {
      log.error("# Error: " + res.getStatusCode());
      return null;
    }
    log.info("> " + data.lista().size() + " devices loaded on page " + page);
    return data;
  }

  @SuppressWarnings("null")
  public List<Client> loadClients() throws IOException {
    headers.set("Authorization", milvusKey);
    entity = new HttpEntity<String>(headers);
    String url = baseUrl + "/cliente/busca";
    ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    String body = res.getBody();
    ClientResp data = mapper.readValue(body, ClientResp.class);
    return data.mapToClient();
  }

  public TicketResp loadTickets() throws IOException {
    TicketResp tickets = loadTicketsByPage("1");
    if (tickets.meta().paginate().last_page() > 1) {
      for (Integer i = 2; i <= tickets.meta().paginate().last_page(); i++) {
        TicketResp pageData = loadTicketsByPage(i.toString());
        tickets.lista().addAll(pageData.lista());
      }
    }
    return tickets;
  }

  @SuppressWarnings("null")
  public TicketResp loadTicketsByPage(String page) throws IOException {
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
    ResponseEntity<TicketResp> res = restTemplate.exchange(url, HttpMethod.POST, entity, TicketResp.class);
    return res.getBody();
  }

  @SuppressWarnings("null")
  public TicketResp loadNewRuntalentInTickets() throws IOException {
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
    ResponseEntity<TicketResp> res = restTemplate.exchange(url, HttpMethod.POST, entity, TicketResp.class);
    return res.getBody();
  }

  @SuppressWarnings("null")
  public void createTicket(HashMap<String, String> data) throws IOException {
    headers.set("Authorization", milvusKey);
    String payload = mapper.writeValueAsString(data);
    entity = new HttpEntity<String>(payload, headers);
    String url = baseUrl + "/chamado/criar";
    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
  }

  @SuppressWarnings("null")
  public TicketResp loadMounthRuntalentInTickets() throws IOException {
    LocalDate firstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate lastDay = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    payload = String.format(payload, firstDay.format(formatter), lastDay.format(formatter));
    entity = new HttpEntity<String>(payload, headers);
    String url = baseUrl + "/chamado/listagem?is_descending=true&order_by=codigo&total_registros=100";
    ResponseEntity<TicketResp> res = restTemplate.exchange(url, HttpMethod.POST, entity, TicketResp.class);
    return res.getBody();
  }
}