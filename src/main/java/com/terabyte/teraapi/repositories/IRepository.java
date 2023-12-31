package com.terabyte.teraapi.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public interface IRepository<T> {
  @Autowired
  JdbcTemplate jdbcTemplate = new JdbcTemplate();

  String GET_ALL = "";
  String CREATE = "";
  String DELETE = "";
  String UPDATE = "";
  String UPSERT = "";

  public List<T> getAll();
  public Integer create(T t);
  public Integer update(T t);
  public Integer delete(Integer id);
  public void upsert(T t);
}
