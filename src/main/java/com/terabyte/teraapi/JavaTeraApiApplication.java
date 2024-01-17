package com.terabyte.teraapi;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.terabyte.teraapi.services.BitdefenderService;
import com.terabyte.teraapi.services.MilvusService;


@SpringBootApplication
public class JavaTeraApiApplication {
	Logger log =  LoggerFactory.getLogger(JavaTeraApiApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(JavaTeraApiApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:3000");
			}
		};
	}

	// @Bean
	// ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(BitdefenderService bitdefenderService,
	// 		MilvusService milvusService) {
	// 	return event -> {
	// 		log.info("- Start sync at " + Date.from(new Date().toInstant()));
	// 		long start = System.currentTimeMillis();
	// 		try {
	// 			// milvusService.syncClients();
	// 			milvusService.syncDevices();
	// 			// bitdefenderService.syncSecurityStatus();
	// 		} catch (Exception e) {
	// 			log.error("# Error: " + e.getMessage());
	// 		}
	// 		long end = System.currentTimeMillis();
	// 		log.info("- End sync at " + Date.from(new Date().toInstant()));
	// 		log.info("- Sync completed in " + ((end - start) / 60000) + "ms");
	// 	};
	// }
}
