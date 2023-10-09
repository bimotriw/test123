package com.oustme.oustsdk.launcher.OustExceptions;

import java.io.PrintStream;

/**
 * Created by shilpysamaddar on 25/04/17.
 */

public class SecretKeyNotFound extends OustException {

    public SecretKeyNotFound() {
    }

    public SecretKeyNotFound(String message) {
        super(message);
    }

    public SecretKeyNotFound(Throwable cause) {
        super(cause);
    }


    @Override
    public String getMessage() {
        return "Secret key is not found in Manifest meta data";
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }
}
