package com.terabyte.teraapi.config;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.terabyte.teraapi.services.BitdefenderService;
import com.terabyte.teraapi.services.MilvusService;

@Configuration
@EnableScheduling
public class SyncCronConfig {

  @Autowired
  private final MilvusService milvusService = new MilvusService();
  @Autowired
  private final BitdefenderService bitdefenderService = new BitdefenderService();

  Logger log = LoggerFactory.getLogger("SyncData");

  @Scheduled(cron = "0 0 0 * * *")
  public void syncData() {
    log.info("\n- Start sync at " + Date.from(new Date().toInstant()));
    long start = System.currentTimeMillis();
    try {
      milvusService.syncClients();
      milvusService.syncDevices();
      bitdefenderService.syncSecurityStatus();
    } catch (Exception e) {
      log.error("# Error: " + e.getMessage());
    }
    long end = System.currentTimeMillis();
    log.info("- End sync at " + Date.from(new Date().toInstant()));
    log.info("- Sync completed in " + ((end - start) / 60000) + "min\n");
  }
}