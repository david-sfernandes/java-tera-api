package com.terabyte.teraapi.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DbConfig {
  @Value("${db.url}")
  String url;
  @Value("${db.username}")
  String username;
  @Value("${db.password}")
  String password;

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);

    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
    resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));
    resourceDatabasePopulator.execute(dataSource);

    return dataSource;
  }
}
