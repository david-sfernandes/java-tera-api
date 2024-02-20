package com.terabyte.teraapi.models.external.securityStatus;

import java.util.List;

public record CompaniesResults(Integer total, Integer page, Integer perPage, Integer pagesCount,
    List<SecurityGroups> items) {
}
