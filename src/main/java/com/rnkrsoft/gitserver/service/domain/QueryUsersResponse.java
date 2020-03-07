package com.rnkrsoft.gitserver.service.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryUsersResponse {
    int total;
    int pageNo;
    final List<Record> records = new ArrayList<Record>();
    public QueryUsersResponse addRecord(Record record){
        records.add(record);
        return this;
    }
    @Getter
    @ToString
    @AllArgsConstructor
    public static class Record {
        String username;
        String email;
        String valid;
    }
}
