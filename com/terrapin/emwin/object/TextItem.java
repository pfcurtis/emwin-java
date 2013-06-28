
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
    public String body = null;
    
    public TextItem() {
    	
    }
    
    public TextItem(Packet p) throws UnsupportedEncodingException {
    	body = new String(p.getBody(), "ISO-8859-1");
    	super.packetDate = p.fd;
    	super.packetFileName = p.fn;
    	super.packetFileType = p.ft;
    }
    
}
