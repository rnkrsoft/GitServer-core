package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;

@Data
public class CreateRepositoryRequest {
    String repositoryName;
    String owner;
}
