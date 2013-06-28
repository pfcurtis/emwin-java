
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.util.Date;
import java.text.ParseException;

import com.terrapin.emwin.EMWINScanner;
import com.terrapin.emwin.EMWINValidator;

/**
 * This class represents a single packet received on the EMWIN data stream
 *
 * @author pcurtis
 *
 */
public class Packet implements Serializable {

    private String header = null;
    private byte[] body = null;
    private static final long serialVersionUID = 2L;
    /**
     * The eight character file name
     */
    public String fn;
    /**
     * The three character file type (GIF, TXT, etc.)
     */
    public String ft;
    /**
     * The file creation date (as transmitted from NOAA) as a Java Date
     */
    public Date fd;
    /**
     * The total number of packets of this filename/filetype
     */
    public int pt;
    /**
     * The sequence number of this packet (part number)
     */
    public int pn;
    /**
     * The checksum of this packet as transmitted
     */
    public int cs;
    private boolean headerValid = false;
    private boolean checksumValid = false;
    private long cksum = 0;
    private EMWINValidator v;

    private Packet() {
    }

    public Packet(EMWINValidator vin) {
        v = vin;
    }
    /**
     * Set the header string of this packet. Used in the scanner
     * @see EMWINScanner
     * @param s
     * @throws PacketException
     * @throws ParseException
     */
    public void setHeader(String s) throws PacketException, ParseException {
        header = s;
        headerValid = v.checkHeader(this);
    }

    /**
     * Returns the packet header as a String without line terminators
     *
     * @return packet header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the packet body as a byte array. Used by the scanner
     * @see EMWINScanner
     * @param b
     * @throws PacketException
     */
    public void setBody(byte[] b) throws PacketException {
        if (header == null)
            throw new PacketException("Packet Header not set");
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
     * @return true if packet body checksum matches
     */
    public boolean isChecksumValid() {
        return checksumValid;
    }

    /**
     * Is the packet header valid? This is determined by using a regular expression to extract the relevant parts of the transmitted header.
     * @see EMWINScanner
     *
     * @return true if header was parsed correctly
     */
    public boolean isHeaderValid() {
        return headerValid;
    }

    /**
     * Set the header valid flag
     * @param b
     */
    public void headerValid(boolean b) {
        headerValid = b;
    }

    /**
     * Return the calculated checksum of the packet body
     * @return checksum value
     */
    public long getCalculatedChecksum() {
        return cksum;
    }

    /**
     * This method should be used to determined whether further processing of the packet should be performed
     * @return true if the packet is completely valid
     */
    public boolean isPacketValid() {
        return (headerValid && checksumValid);
    }
}
