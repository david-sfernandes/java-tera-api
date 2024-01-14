package com.terabyte.teraapi.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terabyte.teraapi.services.MilvusService;
import com.terabyte.teraapi.utils.tickets.MilvusCleanTicket;
import com.terabyte.teraapi.utils.tickets.MilvusTicketResp;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  @Autowired
  private final MilvusService milvusService = new MilvusService();

  @GetMapping()
  public List<MilvusCleanTicket> getTickets() throws IOException {
    MilvusTicketResp resp = milvusService.loadTickets();
    return resp.format();
  }
}
