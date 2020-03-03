package com.rnkrsoft.gitserver.http;

import java.io.Serializable;

public class AjaxRequest<T> implements Serializable {
    String action;
    T data;

    public void setAction(String action) {
        this.action = action;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public T getData() {
        return data;
    }
}
