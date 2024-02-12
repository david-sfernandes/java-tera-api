package com.terabyte.teraapi.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.terabyte.teraapi.services.ScheduleService;
import com.terabyte.teraapi.services.SyncService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SyncCronConfig {
  @Autowired
  private final SyncService syncService = new SyncService();
  @Autowired
  private final ScheduleService scheduleService = new ScheduleService();

  @Scheduled(cron = "0 0 0 * * *")
  public void syncData() {
    log.info("\n- Start sync at " + Date.from(new Date().toInstant()));
    syncService.syncAllData();
    scheduleService.scheduleRuntalentTickets();
    log.info("- End sync at " + Date.from(new Date().toInstant()));
  }
}
