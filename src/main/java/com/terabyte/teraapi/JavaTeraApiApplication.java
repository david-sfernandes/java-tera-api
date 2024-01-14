package com.terabyte.teraapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import com.terabyte.teraapi.services.BitdefenderService;
import com.terabyte.teraapi.services.MilvusService;

@SpringBootApplication
public class JavaTeraApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaTeraApiApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(BitdefenderService bitdefenderService,
			MilvusService milvusService) {
		return event -> {
			long start = System.currentTimeMillis();
			try {
				// Sync Client
				milvusService.syncDevices();
				// bitdefenderService.syncSecurityStatus();
				// Sync Ticket
			} catch (Exception e) {
				System.out.println("# Error: " + e.getMessage());
			}
			long end = System.currentTimeMillis();
			System.out.println("> Sync completed in " + ((end - start) / 60000) + "ms");
		};
	}
}
