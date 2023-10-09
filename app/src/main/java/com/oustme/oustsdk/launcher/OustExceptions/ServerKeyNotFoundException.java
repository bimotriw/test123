package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class ServerKeyNotFoundException extends OustException {
    public ServerKeyNotFoundException() {
    }

    public ServerKeyNotFoundException(String message) {
        super(message);
    }

    public ServerKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Server Key can not be null or empty in Push Notification configuration parameters";
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(stackTrace);
    }
}
