/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * The base class for all EMWIN items. An item is a complete EMWIN document or image with all parts assembled.
 * @see TextItem
 * 
 * @author pcurtis
 *
 */
public class Item implements Serializable {

	private Date packetDate = null;
	private String packetFileName = null;
	private String packetFileType = null;
	
	private Date issue = null;
	private Date expires = null;
	private String title = null;
	private String mtype = null;
	private String wid = null;
	private Map<Zone, Zone> zones = null;
	
	public Item() {
		
	}

	/**
	 * @return the packetDate
	 */
	public Date getPacketDate() {
		return packetDate;
	}

	/**
	 * @param packetDate the packetDate to set
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
	 * @param packetFileName the packetFileName to set
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
	 * @param packetFileType the packetFileType to set
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
	 * @param issue the issue to set
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
	 * @param expires the expires to set
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
	 * @param title the title to set
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
	 * @param mtype the mtype to set
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
	 * @param wid the wid to set
	 */
	public void setWid(String wid) {
		this.wid = wid;
	}

	/**
	 * @return the zones
	 */
	public Map<Zone, Zone> getZones() {
		return zones;
	}

	/**
	 * @param zones the zones to set
	 */
	public void setZones(Map<Zone, Zone> zones) {
		this.zones = zones;
	}
}
