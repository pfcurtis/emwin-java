/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author pcurtis
 *
 */
public class Item implements Serializable {

	public Date packetDate = null;
	public String packetFileName = null;
	public String packetFileType = null;
	
	public Date issue = null;
	public Date expires = null;
	public String title = null;
	public String mtype = null;
	public String wid = null;
	public Map<Zone, Zone> zones = null;
	
	public Item() {
		
	}
}
