package com.rnkrsoft.gitserver.service.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 2020/3/5.
 */
@Data
public class ListUsersResponse {
   final List<String> users = new ArrayList<String>();
}
