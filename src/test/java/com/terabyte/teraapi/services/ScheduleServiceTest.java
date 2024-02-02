package com.terabyte.teraapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.repositories.TicketsQueueRepository;
import com.terabyte.teraapi.utils.tickets.MilvusTicket;
import com.terabyte.teraapi.utils.tickets.MilvusTicketResp;
import com.terabyte.teraapi.models.Client;
import com.terabyte.teraapi.models.TicketsQueue;

@JdbcTest
@ContextConfiguration(classes = { ScheduleService.class })
@SpringBootTest
@ActiveProfiles("test")
public class ScheduleServiceTest {
  @Mock
  private MilvusService milvusService;
  @Mock
  private TicketsQueueRepository ticketsQueueRepository;
  @Mock
  private ClientRepository clientRepository;
  @InjectMocks
  private ScheduleService scheduleService;

  @Test
  @DisplayName("Should schedule Runtalent tickets for new IN Forms")
  public void scheduleRuntalentTicketsTest() throws IOException {
    MilvusTicketResp tickets = createMockTickets();
    LocalDate currentDate = LocalDate.now();
    List<TicketsQueue> ticketsQueue = new ArrayList<TicketsQueue>();
    when(milvusService.loadNewRuntalentInTickets()).thenReturn(tickets);
    when(clientRepository.getByName("client")).thenReturn(createMockClient());

    scheduleService.scheduleRuntalentTickets();

    assertEquals(1, ticketsQueue.size());
    assertEquals("123", ticketsQueue.get(0).getTicketId());
    assertEquals(1, ticketsQueue.get(0).getClientId());
    assertEquals(Date.valueOf(currentDate.plusDays(45)), ticketsQueue.get(0).getFirstDate());
    assertEquals(Date.valueOf(currentDate.plusDays(90)), ticketsQueue.get(0).getSecondDate());
    assertEquals(false, ticketsQueue.get(0).getIsFirstOpen());
    assertEquals(false, ticketsQueue.get(0).getIsSecondOpen());
  }

  public MilvusTicket createMockTicket() {
    return new MilvusTicket(
        123,
        "categoria_primaria",
        "categoria_secundaria",
        1,
        "tecnico",
        "mesa_trabalho",
        "data_solucao",
        null,
        "data_resposta",
        "setor",
        "prioridade",
        123,
        "cliente",
        "assunto",
        "contato",
        "data_criacao",
        "total_horas",
        "origem",
        "status",
        "urgencia",
        null,
        "status_sla_resposta",
        "status_sla_solucao");
  }

  public MilvusTicketResp createMockTickets() {
    List<MilvusTicket> lista = new ArrayList<MilvusTicket>();
    lista.add(createMockTicket());
    return new MilvusTicketResp(null, lista);
  }

  public Client createMockClient() {
    return Client.builder().id(1).name("client").build();
  }
}
