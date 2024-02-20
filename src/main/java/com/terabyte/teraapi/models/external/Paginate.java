package com.terabyte.teraapi.models.external;

public record Paginate(Integer current_page, Integer total, Integer to, Integer from, Integer last_page,
    String per_page) {

}
