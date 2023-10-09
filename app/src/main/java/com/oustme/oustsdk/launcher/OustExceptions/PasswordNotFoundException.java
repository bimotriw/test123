package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class PasswordNotFoundException extends OustException {

    public PasswordNotFoundException() {
    }

    public PasswordNotFoundException(String message) {
        super(message);
    }

    public PasswordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Password can not be null in Oust Auth Data";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
