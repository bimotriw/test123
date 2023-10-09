package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class TokenNotFoundException extends OustException {
    public TokenNotFoundException() {
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Token can not be null or empty in Push Notification Configuration parameters";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
