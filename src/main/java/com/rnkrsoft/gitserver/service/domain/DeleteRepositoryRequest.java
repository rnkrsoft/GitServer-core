package com.rnkrsoft.gitserver.service.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by test on 2020/3/5.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteRepositoryRequest {
    int repositoryId;
}
