package com.oustme.oustsdk.launcher.OustExceptions;

/**
 * Created by shilpysamaddar on 25/04/17.
 */

public class InvalidSecretKey extends OustException {
    public InvalidSecretKey() {
        super();
    }

    public InvalidSecretKey(String message) {
        super(message);
    }

    public InvalidSecretKey(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSecretKey(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        return "Secret Key is invalid";
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
