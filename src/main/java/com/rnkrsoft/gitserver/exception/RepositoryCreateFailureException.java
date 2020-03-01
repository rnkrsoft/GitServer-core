package com.rnkrsoft.gitserver.exception;

/**
 * Created by woate on 2020/02/24.
 */
public class RepositoryCreateFailureException extends Exception {
    public RepositoryCreateFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
