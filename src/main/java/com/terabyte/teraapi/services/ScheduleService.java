package com.terabyte.teraapi.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terabyte.teraapi.repositories.ClientRepository;
import com.terabyte.teraapi.repositories.TicketsQueueRepository;
import com.terabyte.teraapi.utils.tickets.MilvusTicket;
import com.terabyte.teraapi.utils.tickets.MilvusTicketResp;

import org.slf4j.Logger;

import com.terabyte.teraapi.models.TicketsQueue;

@Service
public class ScheduleService {
  @Autowired
  private MilvusService milvusService;
  @Autowired
  private TicketsQueueRepository ticketsQueueRepository;
  @Autowired
  private ClientRepository clientRepository;

  Logger logger = LoggerFactory.getLogger(ScheduleService.class);

  public void scheduleRuntalentTickets() {
    logger.info("Scheduling Runtalent tickets");
    MilvusTicketResp tickets;
    LocalDate currentDate = LocalDate.now();
    List<TicketsQueue> ticketsQueue = new ArrayList<TicketsQueue>();

    try {
      tickets = milvusService.loadNewRuntalentInTickets();
    } catch (Exception e) {
      logger.error("Error loading tickets from Milvus", e);
      return;
    }
    Integer clientId = clientRepository
        .getByName(tickets.lista().get(0).cliente())
        .getId();

    for (MilvusTicket ticket : tickets.lista()) {
      ticketsQueue.add(
          TicketsQueue.builder()
              .ticketId(ticket.codigo())
              .clientId(clientId)
              .firstDate(Date.valueOf(currentDate.plusDays(45)))
              .secondDate(Date.valueOf(currentDate.plusDays(90)))
              .isFirstOpen(false)
              .isSecondOpen(false)
              .build());
    }
    logger.info("Inserting tickets " + ticketsQueue.size() + " into database");
    ticketsQueueRepository.batchInsert(ticketsQueue);
  }

  public void openTicketsFromQueue() {
    logger.info("Opening tickets from queue");
    openFirstRuntalentTickets();
    openSecondRuntalentTickets();
  }

  public void openFirstRuntalentTickets() {
    String currentDate = LocalDate.now().toString();
    List<TicketsQueue> ticketsQueue = ticketsQueueRepository.getTicketsToOpenFirst(currentDate);
    
    for (TicketsQueue ticket : ticketsQueue) {
      try {
        HashMap<String, String> payload = createTicketPayload(ticket.getTicketId());
        milvusService.createTicket(payload);
        logger.info(ticketsQueue.size() + " tickets opened successfully on first check");
        ticketsQueueRepository.batchUpdateIsFirstOpen(ticketsQueue, true);
      } catch (Exception e) {
        logger.error("Error opening ticket " + ticket.getTicketId(), e);
      }
    }
  }

  public void openSecondRuntalentTickets() {
    logger.info("Opening first Runtalent tickets");
    String currentDate = LocalDate.now().toString();
    List<TicketsQueue> ticketsQueue = ticketsQueueRepository.getTicketsToOpenSecond(currentDate);

    for (TicketsQueue ticket : ticketsQueue) {
      try {
        HashMap<String, String> payload = createTicketPayload(ticket.getTicketId());
        milvusService.createTicket(payload);
        logger.info(ticketsQueue.size() + " tickets opened successfully on second check");
        ticketsQueueRepository.batchUpdateIsSecondOpen(ticketsQueue, true);
      } catch (Exception e) {
        logger.error("Error opening ticket " + ticket.getTicketId(), e);
      }
    }
  }

  public HashMap<String, String> createTicketPayload(Integer ticketId) {
    HashMap<String, String> payload = new HashMap<>();
    // Milvus API use the client TOKEN instead of the client ID
    payload.put("cliente_id", "7GZX9I"); 
    payload.put("chamado_assunto", "Verificação de Inventario (retorno ticket " + ticketId.toString() + ")");
    payload.put("chamado_descricao",
        "Entrar em contato com usuário e verificar se ele tem algum dispositivo da Runtalent com ele. Caso não tenha questionar se ele tem dispositivo entregue pelo cliente que ele está alocado. Se o usuário tiver uma maquina entregue pelo cliente devemos solicitar a service tag, ou dados de processado, memória e HD");
    payload.put("chamado_mesa", "Help Desk");
    payload.put("chamado_setor", "TI");
    payload.put("chamado_categoria_primaria", "Cadastros e Acessos");
    payload.put("chamado_contato", "Automação");
    return payload;
  }
}
