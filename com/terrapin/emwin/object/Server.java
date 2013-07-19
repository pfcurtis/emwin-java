/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.Serializable;

/**
 * This class represents a single EMWIN (ByteBlast) protocol server
 * 
 * @author pcurtis
 *
 */
public class Server implements Serializable {

    private String host = null;
    private int port = 0;
    public boolean usable = true;

    /**
     * 
     * @param h host name or IP address
     * @param p port number
     */
    public Server(String h, int p) {
        host = h;
        port = p;
    }

    /**
     * 
     * @param s server list string
     */
    public Server(String s) {
        String[] server = s.split(":");
        this.host = new String(server[0]);
        this.port = new Integer(server[1]).intValue();
    }
    
    private Server() {
    }
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
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
        Server other = (Server) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        return true;
    }
    
}
