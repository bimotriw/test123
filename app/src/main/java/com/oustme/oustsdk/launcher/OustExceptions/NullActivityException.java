package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class NullActivityException extends OustException {

    public NullActivityException() {
    }

    public NullActivityException(String message) {
        super(message);
    }

    public NullActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Activity can not be null in OustLauncher parameters";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
