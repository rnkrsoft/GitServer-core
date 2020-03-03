package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryRepositoryResponse {
    int total;
    int pageNo;
    final List<Record> records = new ArrayList<Record>();
    public QueryRepositoryResponse addRecord(Record record){
        records.add(record);
        return this;
    }
    @Getter
    @AllArgsConstructor
    public static class Record {
        String repositoryName;
        String repositoryDesc;
        String createDate;
        String lastUpdateDate;
    }
}
