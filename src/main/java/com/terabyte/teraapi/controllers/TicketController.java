package com.terabyte.teraapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.models.Ticket;
import com.terabyte.teraapi.repositories.TicketRepository;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  @Autowired
  private final TicketRepository ticketRepository = new TicketRepository();

  @GetMapping()
  public List<Ticket> getTickets() {
    return ticketRepository.getAll();
  }
}
