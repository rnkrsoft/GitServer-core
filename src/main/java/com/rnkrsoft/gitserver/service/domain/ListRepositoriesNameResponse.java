package com.rnkrsoft.gitserver.service.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 2020/3/5.
 */
public class ListRepositoriesNameResponse {
    @Getter
    final List<String> repositoriesName = new ArrayList<String>();
}
