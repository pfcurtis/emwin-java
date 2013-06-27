
package com.terrapin.emwin;

import java.util.Date;
import java.text.ParseException;

/**
 * This class represents a single packet received on the EMWIN data stream
 * 
 * @author pcurtis
 *
 */
public class EMWINPacket {

    private String header = null;
    private byte[] body = null;
    /**
     * The eight character file name
     */
    public String fn;
    /**
     * The three character file type (GIF, TXT, etc.)
     */
    public String ft;
    /**
     * The file creation date as a Java Date
     */
    public Date fd;
    /**
     * The total number of packets of this filename/filetype 
     */
    public int pn;
    /**
     * The sequence number of this packet (part number)
     * 
     * @see pn
     */
    public int pt;
    /**
     * The checksum of this packet as transmitted
     */
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
/**
 * Set the header string of this packet. Used in the scanner
 * @see EMWINScanner
 * @param s
 * @throws EMWINPacketException
 * @throws ParseException
 */
    public void setHeader(String s) throws EMWINPacketException, ParseException {
        header = s;
        headerValid = v.checkHeader(this);
    }

    /**
     * Returns the packet header as a String without line terminators
     * 
     * @return 
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the packet body as a byte array. Used by the scanner
     * @see EMWINScanner
     * @param b
     * @throws EMWINPacketException
     */
    public void setBody(byte[] b) throws EMWINPacketException {
        if (header == null)
            throw new EMWINPacketException("Packet Header not set");
        body = b;
        cksum = v.calculateChecksum(this);
        checksumValid = (cksum == cs);
    }

    /**
     * Returns the packet body as a byte array
     *
     * @return packet body
     */
    public byte[] getBody() {
        return body;
    }

    /** Does the checksum match what was transmitted from the data source?
     * 
     * @return 
     */
    public boolean isChecksumValid() {
        return checksumValid;
    }

    /**
     * Is the packet header valid? This is determined by using a regular expression to extract the relevant parts of the transmitted header.
     * @see EMWINScanner
     * 
     * @return
     */
    public boolean isHeaderValid() {
        return headerValid;
    }

    public void headerValid(boolean b) {
        headerValid = b;
    }

    public long getCalculatedChecksum() {
        return cksum;
    }

    /**
     * This method should be used to determined whether further processing of the packet should be performed
     * @return
     */
    public boolean isPacketValid() {
        return (headerValid && checksumValid);
    }
}
