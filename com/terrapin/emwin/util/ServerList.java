/**
 * 
 */
package com.terrapin.emwin.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.EMWINConnection;
import com.terrapin.emwin.EMWINProperties;
import com.terrapin.emwin.object.Server;
import com.terrapin.emwin.storm.EMWINTopology;

/**
 * @author pcurtis
 *
 */
public class ServerList implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Hashtable<String, Server> sl;
    private ArrayList<String> serverSequence = new ArrayList<String>();
    private volatile int serverIndex = 0;
    
    private final Logger log = LoggerFactory.getLogger(ServerList.class);
    
    public ServerList() {
        sl = new Hashtable<String, Server>();
        readServerList();
        if (sl.isEmpty()) {
            log.info("Initializing server list with default values.");
            setServerList("ServerList/140.90.6.245:1000|140.90.128.132:1000|140.90.128.133:1000|");
        }
    }
    
    /**
     * 
     */
    public void setServerList(String s) {
        // ServerList/198.105.228.6:1000|140.90.128.1:1000|216.248.137.10:2211|108.76.168.147:1000|24.54.148.4:2211|wx.2y.net:2211|emwin.aprsfl.net:2211|p1.wxpro.net:2211|6.pool.iemwin.net:2211|2.pool.iemwin.net:2211|140.90.128.132:1000|1.pool.iemwin.net:2211|w.2y.net:1000|140.90.128.133:1000|3.pool.iemwin.net:2211|140.90.6.245:1000|140.90.24.30:22|140.90.24.118:22|76.107.51.99:1000|71.43.225.58:2211|140.90.6.240:1000|184.6.183.200:1000|
        synchronized (serverSequence) {
            serverSequence.clear();
            s = s.substring(s.indexOf('/') + 1);
            while (s.indexOf('|') != -1) {
                String server = s.substring(0, s.indexOf('|'));
                serverSequence.add(server);
                log.debug("adding server '"+server+"'");
                int start = s.indexOf('|') + 1;
                s = s.substring(start);
            }
            Hashtable<String, Server> newList = new Hashtable<String, Server>();
            Iterator<String> i = serverSequence.iterator();
            while (i.hasNext()) {
                String value = i.next();
                log.debug("Checking server '"+value+"'");
                if (sl.containsKey(value)) {
                    Server known = (Server)sl.get(value);
                    newList.put(value,known);
                } else {
                    newList.put(value,  new Server(value));
                }
            }
            sl = newList;
            serverIndex = 0;
            writeServerList();
        }

        log.info("new Server List received, "+ sl.size() + " servers.");

    }
    
    /**
     * 
     * @return current available host name
     */
    public String getHost() {
        String key = serverSequence.get(serverIndex);
        return ((Server)sl.get(key)).getHost();
    }
    
    /**
     * 
     * @return current available host port
     */
    public int getPort() {
        String key = serverSequence.get(serverIndex);
        return ((Server)sl.get(key)).getPort();
    }
    
    /**
     * Mark the current server as unavailable. This is tracked, so the server will permanently be marked as unavailable.
     */
    public void markServerDisabled() {
        synchronized (serverSequence) {
            String key = serverSequence.get(serverIndex);
            Server s = (Server)sl.get(key);
            s.usable = false;
            writeServerList();
            serverIndex++;
            if (serverIndex >= serverSequence.size()) {
                serverIndex = 0;
            }
        }
    }

    /**
     * Increments the server index. This is only used when a manual disconnect is sent to the spout.
     */
    public void nextServer() {
        serverIndex++;
        if (serverIndex >= serverSequence.size()) {
            serverIndex = 0;
        }
    }
    
    /**
     * This method serializes the server list and writes it out to a file for future use.
     */
    private void writeServerList() {

        try {
            //use buffering
            OutputStream file = new FileOutputStream( EMWINProperties.loadProperties().getProperty("config.directory", "./") + "/server_list.ser" );
            ObjectOutput output = new ObjectOutputStream( file );
            try {
                output.writeObject(sl);
            } finally {
                output.close();
            }
        }  
        catch(IOException ex){
            log.error("Cannot perform output.", ex);
        }

    }
    
    /**
     * This method reads the server list from disk, if it exists
     */
    @SuppressWarnings("unchecked")
    private void readServerList() {
        InputStream file = null;
        try {
            file = new FileInputStream( EMWINProperties.loadProperties().getProperty("config.directory", "./") + "/server_list.ser" );
            try {
                ObjectInputStream input = new ObjectInputStream ( file );
                log.info("Reading server list file ....");
                sl = (Hashtable<String, Server>)input.readObject();
                input.close();
                Enumeration<String> e = sl.keys();
                while (e.hasMoreElements()){
                    serverSequence.add(e.nextElement());
                }
            } catch(Exception e1) {
                log.info("Cannot read 'Hashtable' from server list file.", e1);
            }    
        } catch (FileNotFoundException e1) {
            log.info("server list file not found.");
        }
    }


}
