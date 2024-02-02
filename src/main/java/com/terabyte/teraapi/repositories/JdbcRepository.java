package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public interface JdbcRepository<T> {
  @Autowired
  JdbcTemplate jdbcTemplate = new JdbcTemplate();

  String GET_ALL = "";
  String CREATE = "";
  String DELETE = "";
  String UPDATE = "";

  public List<T> getAll();
  public Integer create(T t);
  public Integer update(T t);
  public Integer delete(Integer id);
}
