
package com.terrapin.emwin;

import java.util.Date;
import java.text.ParseException;

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
    private long cksum = 0;
    private EMWINValidator v;

    private EMWINPacket() {
    }

    public EMWINPacket(EMWINValidator vin) {
        v = vin;
    }

    public void setHeader(String s) throws EMWINPacketException, ParseException {
        header = s;
        headerValid = v.checkHeader(this);
    }

    public String getHeader() {
        return header;
    }

    public void setBody(byte[] b) throws EMWINPacketException {
        if (header == null)
            throw new EMWINPacketException("Packet Header not set");
        body = b;
        cksum = v.calculateChecksum(this);
        checksumValid = (cksum == cs);
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

    public long getCalculatedChecksum() {
        return cksum;
    }

    public boolean isPacketValid() {
        return (headerValid && checksumValid);
    }
}
