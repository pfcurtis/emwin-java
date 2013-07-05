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

    public Server(String h, int p) {
        host = h;
        port = p;
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
    
}
