package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class UserNameNotFoundException extends OustException {
    public UserNameNotFoundException() {
    }

    public UserNameNotFoundException(String message) {
        super(message);
    }

    public UserNameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "User name not found in Oust Auth Data";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
