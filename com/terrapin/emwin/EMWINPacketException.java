

package com.terrapin.emwin;

public class EMWINPacketException extends Exception {

    private String message;

    public EMWINPacketException(String s) {
        super(s, new Throwable());
        System.err.println(s);
        message = s;
    }

    public String getError() {
        return message;
    }
}
