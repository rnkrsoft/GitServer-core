package com.rnkrsoft.gitserver.service.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryUsersRequest {
    String userName;
    boolean userNameLike;
    int pageSize;
    int pageNo;
}
