package com.terabyte.teraapi.models.external.tickets;

import java.util.List;
import java.util.stream.Collectors;

import com.terabyte.teraapi.models.external.Meta;

public record TicketResp(Meta meta, List<MilvusTicket> lista) {

  public List<CleanTicket> format() {
    return lista.stream().map(MilvusTicket::format).collect(Collectors.toList());
  }
}
