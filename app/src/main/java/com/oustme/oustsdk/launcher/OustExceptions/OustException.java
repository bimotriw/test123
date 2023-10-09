package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 25/04/17.
 */

public class OustException extends Exception {
    public OustException() {
    }

    public OustException(String message) {
        super(message);
    }

    public OustException(String message, Throwable cause) {
        super(message, cause);
    }

    public OustException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
