package com.terabyte.teraapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class JavaTeraApiApplication {
  Logger log = LoggerFactory.getLogger(JavaTeraApiApplication.class);

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

  // @Bean
  // ApplicationListener<ApplicationReadyEvent>
  // basicsApplicationListener(MilvusService milvusService) {
  // return event -> {
  // try {

  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // };
  // }
}
