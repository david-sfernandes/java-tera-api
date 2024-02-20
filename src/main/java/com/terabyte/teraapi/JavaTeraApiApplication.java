package com.terabyte.teraapi;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.terabyte.teraapi.services.SyncService;

@SpringBootApplication
public class JavaTeraApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(JavaTeraApiApplication.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  @Bean
  ApplicationListener<ApplicationReadyEvent> basicsApplicationListener(SyncService service) {
    return event -> {
      try {
        System.out.println("\n- Start sync at " + Date.from(new Date().toInstant()));
        service.syncSecurityStatus();
        System.out.println("- End sync at " + Date.from(new Date().toInstant()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }
}
