/**
 * 
 */
package com.terrapin.emwin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.Server;
import com.terrapin.emwin.storm.EMWINSpout;
import com.terrapin.emwin.storm.EMWINTopology;
import com.terrapin.emwin.util.ServerList;

/**
 * This class manages and maintains the network connection to the server(s).
 * 
 * @author pcurtis
 *
 */
public class EMWINConnection implements Serializable {

    private ServerList sl;
    private Socket emwinSocket = null;
    private OutputStream out = null;
    private EMWINInputStream in = null;
    private Timer t;
    private Properties props;
    private String configDirectory;

    private final Logger log = LoggerFactory.getLogger(EMWINConnection.class);

    public EMWINConnection() {
        props = EMWINTopology.loadProperties();
        configDirectory = props.getProperty("config.directory", "./");
        sl = new ServerList();
    }

    public void connect() {

        while (true) {
            try {
                emwinSocket = new Socket(sl.getHost(), sl.getPort());
                out = emwinSocket.getOutputStream();
                in = new EMWINInputStream(emwinSocket.getInputStream());
                log.info("Connected to '" + sl.getHost() +":"+sl.getPort()+"'");
                break;
            } catch (UnknownHostException e) {
                log.error("Don't know about host '" + sl.getHost() + "'", e);
                sl.markServerDisabled();
            } catch (IOException e) {
                log.error("Couldn't get I/O for the connection to '"+sl.getHost()+":"+sl.getPort()+"'", e);
                sl.markServerDisabled();
            } catch (Exception e) {
                log.error("Exception on connection to '"+sl.getHost()+":"+sl.getPort()+"'", e);
                sl.markServerDisabled();
            }
        }

        t = new Timer("Heartbeat", true);
        t.schedule(new EMWINHeartbeat(out), 0, 300000);
    }

    public void close() {

        t.cancel();
        t.purge();

        try {
            out.close();
            in.close();
            emwinSocket.close();
        } catch (IOException e) {
            log.error("Attempting to close() connection", e);
        }
    }


    /**
     * @return the connection input stream
     */
    public EMWINInputStream getIn() {
        return in;
    }

    /**
     * @param in 
     *           the connection input stream to set
     */
    public void setIn(EMWINInputStream in) {
        this.in = in;
    }

    /**
     * This method parses a received server list and updates the server list object.
     * 
     * @param s
     *          String containing the server list
     * @see EMWINScanner
     */
    public void setServerList(String s) {
        sl.setServerList(s);
    }

}