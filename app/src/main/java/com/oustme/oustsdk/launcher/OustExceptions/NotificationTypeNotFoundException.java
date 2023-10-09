package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class NotificationTypeNotFoundException extends OustException {
    public NotificationTypeNotFoundException() {
    }

    public NotificationTypeNotFoundException(String message) {
        super(message);
    }

    public NotificationTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "Notification Type can not be null in Oust Notification Configuration parameters";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
