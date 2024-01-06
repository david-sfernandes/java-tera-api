package com.terabyte.teraapi.utils;

public record MilvusPaginate(Integer current_page, Integer total, Integer to, Integer from, Integer last_page,
    String per_page) {

}
