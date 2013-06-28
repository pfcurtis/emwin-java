/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * The base class for all EMWIN items. An item is a complete EMWIN document or
 * image with all parts assembled.
 * 
 * @see TextItem
 * 
 * @author pcurtis
 * 
 */
public abstract class Item implements Serializable {

    private Date packetDate = null;
    private String packetFileName = null;
    private String packetFileType = null;

    private Date issue = null;
    private Date expires = null;
    private String title = null;
    private String mtype = null;
    private String wid = null;
    private ArrayList<Zone> zones = null;

    public Item() {

    }

    /**
     * @return the packetDate
     */
    public Date getPacketDate() {
        return packetDate;
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

    /**
     * @return the issue
     */
    public Date getIssue() {
        return issue;
    }

    /**
     * @param issue
     *            the issue to set
     */
    public void setIssue(Date issue) {
        this.issue = issue;
    }

    /**
     * @return the expires
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * @param expires
     *            the expires to set
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the mtype
     */
    public String getMtype() {
        return mtype;
    }

    /**
     * @param mtype
     *            the mtype to set
     */
    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    /**
     * @return the wid
     */
    public String getWid() {
        return wid;
    }

    /**
     * @param wid
     *            the wid to set
     */
    public void setWid(String wid) {
        this.wid = wid;
    }

    /**
     * @return the zones
     */
    public ArrayList<Zone> getZones() {
        return zones;
    }

    /**
     * @param zones
     *            the zones to set
     */
    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expires == null) ? 0 : expires.hashCode());
        result = prime * result + ((issue == null) ? 0 : issue.hashCode());
        result = prime * result + ((mtype == null) ? 0 : mtype.hashCode());
        result = prime * result
                + ((packetDate == null) ? 0 : packetDate.hashCode());
        result = prime * result
                + ((packetFileName == null) ? 0 : packetFileName.hashCode());
        result = prime * result
                + ((packetFileType == null) ? 0 : packetFileType.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((wid == null) ? 0 : wid.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
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
        Item other = (Item) obj;
        if (expires == null) {
            if (other.expires != null)
                return false;
        } else if (!expires.equals(other.expires))
            return false;
        if (issue == null) {
            if (other.issue != null)
                return false;
        } else if (!issue.equals(other.issue))
            return false;
        if (mtype == null) {
            if (other.mtype != null)
                return false;
        } else if (!mtype.equals(other.mtype))
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
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (wid == null) {
            if (other.wid != null)
                return false;
        } else if (!wid.equals(other.wid))
            return false;
        return true;
    }

}
