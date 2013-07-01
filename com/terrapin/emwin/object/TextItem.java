package com.terrapin.emwin.object;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * This class represents a text item received. A text item will consist of one or more Packets
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
     * Create a complete EMWIN Text item from a single packet. Used only when
     * there is one part a single Packet) necessary for a complete item,
     * 
     * @see Item
     * @see TextItem
     * @param p the single Packet making up the text item
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
     * @param body
     *            the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((body == null) ? 0 : body.hashCode());
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
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextItem other = (TextItem) obj;
        if (body == null) {
            if (other.body != null)
                return false;
        } else if (!body.equals(other.body))
            return false;
        return true;
    }

}
