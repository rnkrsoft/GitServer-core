package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class QueryRepositoryResponse {
    int total;
    int pageNo;
    final List<Record> records = new ArrayList<Record>();

    @Getter
    @AllArgsConstructor
    static class Record{
        String repositoryName;
        String repositoryDesc;
        String createDate;
        String lastUpdateDate;
    }
}
