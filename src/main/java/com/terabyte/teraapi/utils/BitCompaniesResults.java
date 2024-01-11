package com.terabyte.teraapi.utils;

import java.util.List;

public record BitCompaniesResults(Integer total, Integer page, Integer perPage, Integer pagesCount,
    List<BitGroups> items) {
}
