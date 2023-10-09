package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class NullAuthDataException extends OustException {

    public NullAuthDataException() {
    }

    public NullAuthDataException(String message) {
        super(message);
    }

    public NullAuthDataException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Auth Data can not be null in Oust Launcher parameters";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
