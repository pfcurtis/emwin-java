

package com.terrapin.emwin.object;

public class PacketException extends Exception {

    private String message;

    public PacketException(String s) {
        super(s, new Throwable());
        System.err.println(s);
        message = s;
    }

    public String getError() {
        return message;
    }
}
