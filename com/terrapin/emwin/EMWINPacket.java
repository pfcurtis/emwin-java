
package com.terrapin.emwin;

import java.util.Date;

public class EMWINPacket {

    private String header = null;
    private byte[] body = null;
    public String fn;
    public String ft;
    public Date fd;
    public int pn;
    public int pt;
    public int cs;
    private boolean headerValid = false;
    private boolean checksumValid = false;
    private char cksum = 0;

    public void setHeader(String s) throws EMWINPacketException {
        header = s;
    }

    public String getHeader() {
        return header;
    }

    public void setBody(byte[] b) throws EMWINPacketException {
        if (header == null)
            throw new EMWINPacketException("Packet Header not set");
        body = b;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isChecksumValid() {
       return checksumValid;
    }

    public boolean isHeaderValid() {
       return headerValid;
    }

    public void headerValid(boolean b) {
       headerValid = b;
    }

    public void setCalculatedChecksum(char i) {
       cksum = i;
    }

    public char getCalculatedChecksum() {
        return cksum;
    }
}
