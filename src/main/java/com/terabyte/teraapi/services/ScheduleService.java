package com.terabyte.teraapi.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
}
