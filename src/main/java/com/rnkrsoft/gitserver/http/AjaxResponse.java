package com.rnkrsoft.gitserver.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class AjaxResponse<T> implements Serializable {
    static Gson GSON = new GsonBuilder().create();
    public static String NULL = "{}";
    public static final AjaxResponse SUCCESS = new AjaxResponse(new Result("0000", "成功"), NULL);
    public static final AjaxResponse FAILURE = new AjaxResponse(new Result("9999", "失败"), NULL);

    private Result result;
    private T data;

    public AjaxResponse(Result result, T data) {
        this.result = result;
        this.data = data;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public static class Result{
        String code;
        String desc;

        public Result(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
