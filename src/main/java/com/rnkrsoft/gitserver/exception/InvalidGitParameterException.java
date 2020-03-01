package com.rnkrsoft.gitserver.exception;

/**
 * Created by woate on 2020/02/24.
 */
public class InvalidGitParameterException extends Exception {

    private static final long serialVersionUID = 20111127L;

    public InvalidGitParameterException(String message) {
        super(message);
    }
}
