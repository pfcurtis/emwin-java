
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * This class represents a text item received.
 *
 * @author pcurtis
 *
 */
public class TextItem extends Item implements Serializable {

    private static final long serialVersionUID = 3L;
    private String body = null;
    
    public TextItem() {
    	
    }
    /**
     * Create a complete EMWIN Text item from a single packet. Used only when there is one part necessary for a complete item,
     * @see Item
     * @param p
     * @throws UnsupportedEncodingException
     */
    public TextItem(Packet p) throws UnsupportedEncodingException {
    	body = new String(p.getBody(), "ISO-8859-1");
    	super.setPacketDate(p.fd);
    	super.setPacketFileName(p.fn);
    	super.setPacketFileType(p.ft);
    }

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
    
}
