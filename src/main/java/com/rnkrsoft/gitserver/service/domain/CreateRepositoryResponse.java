package com.rnkrsoft.gitserver.service.domain;

import com.rnkrsoft.gitserver.http.AjaxResponse;

public class CreateRepositoryResponse extends AjaxResponse {
    public CreateRepositoryResponse(Result result, Object data) {
        super(result, data);
    }
}
