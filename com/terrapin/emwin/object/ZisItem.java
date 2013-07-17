/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author pcurtis
 *
 */
public class ZisItem {
    private static final long serialVersionUID = 4L;
    private byte[] body = null;
    private Date packetDate = null;
    private String packetFileName = null;
    private String packetFileType = null;

    public ZisItem() {
    }

    /**
     * Create a complete EMWIN Text item from a single packet. Used only when
     * there is one part a single Packet) necessary for a complete item,
     * 
     * @see TextItem
     * @param p the single Packet making up the text item
     * @throws UnsupportedEncodingException
     */
    public ZisItem(Packet p) {
        body = p.getBody();
        this.setPacketDate(p.fd);
        this.setPacketFileName(p.fn);
        this.setPacketFileType(p.ft);
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body
     *            the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * @return the packetDate
     */
    public Date getPacketDate() {
        return packetDate;
    }

    /**
     * @return long
     *              the packet date as a Unix time field
     */
    public long getPacketDateUtime() {
        return packetDate.getTime() / 1000;
    }
    
    /**
     * @param packetDate
     *            the packetDate to set
     */
    public void setPacketDate(Date packetDate) {
        this.packetDate = packetDate;
    }

    /**
     * @return the packetFileName
     */
    public String getPacketFileName() {
        return packetFileName;
    }

    /**
     * @param packetFileName
     *            the packetFileName to set
     */
    public void setPacketFileName(String packetFileName) {
        this.packetFileName = packetFileName;
    }

    /**
     * @return the packetFileType
     */
    public String getPacketFileType() {
        return packetFileType;
    }

    /**
     * @param packetFileType
     *            the packetFileType to set
     */
    public void setPacketFileType(String packetFileType) {
        this.packetFileType = packetFileType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(body);
        result = prime * result
                + ((packetDate == null) ? 0 : packetDate.hashCode());
        result = prime * result
                + ((packetFileName == null) ? 0 : packetFileName.hashCode());
        result = prime * result
                + ((packetFileType == null) ? 0 : packetFileType.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZisItem other = (ZisItem) obj;
        if (!Arrays.equals(body, other.body))
            return false;
        if (packetDate == null) {
            if (other.packetDate != null)
                return false;
        } else if (!packetDate.equals(other.packetDate))
            return false;
        if (packetFileName == null) {
            if (other.packetFileName != null)
                return false;
        } else if (!packetFileName.equals(other.packetFileName))
            return false;
        if (packetFileType == null) {
            if (other.packetFileType != null)
                return false;
        } else if (!packetFileType.equals(other.packetFileType))
            return false;
        return true;
    }

    
}
