package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 18/04/17.
 */

public class OrgIdNotFoundException extends OustException {

    public OrgIdNotFoundException() {
        super();
    }

    public OrgIdNotFoundException(String message) {
        super(message);
    }

    public OrgIdNotFoundException(String message, Throwable cause) {
        super(message,cause);
    }

    @Override
    public String getMessage() {
        return "OrgId can not be null or empty in Oust Auth Data";
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
