package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class QueryUsersResponse {
    int total;
    int pageNo;
    final List<Record> records = new ArrayList<Record>();

    @Getter
    @AllArgsConstructor
    static class Record{
        String username;
        String email;
        String valid;
    }
}
