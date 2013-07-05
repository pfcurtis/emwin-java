/**
 * 
 */
package com.terrapin.emwin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.Server;
import com.terrapin.emwin.storm.EMWINSpout;

/**
 * This class manages and maintains the network connection to the server(s).
 * 
 * @author pcurtis
 *
 */
public class EMWINConnection implements Serializable {

    private ArrayList<Server> sl = new ArrayList();
    private int serverIndex = 0; 

    private Socket emwinSocket = null;
    private OutputStream out = null;
    private EMWINInputStream in = null;
    private Timer t;

    private final Logger log = LoggerFactory.getLogger(EMWINConnection.class);

    public EMWINConnection() {
        
        // Initialize with the default servers
        // 140.90.6.245:1000
        // 140.90.128.132:1000
        // 140.90.128.133:1000
        sl.add(new Server("140.90.6.245", 1000));
        sl.add(new Server("140.90.128.132", 1000));
        sl.add(new Server("140.90.128.133", 1000));
        
    }
    
    public void connect() {
        
        while (true) {
            Server s = sl.get(serverIndex);
            try {
                log.info("Connecting ... Server " + (serverIndex + 1) +" of " + sl.size() + " servers.");

                emwinSocket = new Socket(s.getHost(), s.getPort());
                out = emwinSocket.getOutputStream();
                in = new EMWINInputStream(emwinSocket.getInputStream());
                log.info("Connected to '" + s.getHost() +":"+s.getPort()+"'");
                break;

            } catch (UnknownHostException e) {
                log.error("Don't know about host '" + s.getHost() + "'", e);
                nextServer();
            } catch (IOException e) {
                log.error("Couldn't get I/O for the connection to '"+s.getHost()+":"+s.getPort()+"'", e);
                nextServer();
            } catch (Exception e) {
                log.error("Exception on connection to '"+s.getHost()+":"+s.getPort()+"'", e);
                nextServer();
            }
        }
        
        t = new Timer("Heartbeat", true);
        t.schedule(new EMWINHeartbeat(out), 0, 300000);
        nextServer();
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
    
    private void nextServer() {
        synchronized (sl) {
            serverIndex++;
            if (serverIndex >= sl.size()) {
                serverIndex = 0;
            }
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
        
        // ServerList/198.105.228.6:1000|140.90.128.1:1000|216.248.137.10:2211|108.76.168.147:1000|24.54.148.4:2211|wx.2y.net:2211|emwin.aprsfl.net:2211|p1.wxpro.net:2211|6.pool.iemwin.net:2211|2.pool.iemwin.net:2211|140.90.128.132:1000|1.pool.iemwin.net:2211|w.2y.net:1000|140.90.128.133:1000|3.pool.iemwin.net:2211|140.90.6.245:1000|140.90.24.30:22|140.90.24.118:22|76.107.51.99:1000|71.43.225.58:2211|140.90.6.240:1000|184.6.183.200:1000|
        synchronized (sl) {
            sl.clear();
            s = s.substring(s.indexOf('/') + 1);
            while (s.indexOf('|') != -1) {
                String[] server = s.substring(0, s.indexOf('|')).split(":");
                sl.add(new Server(server[0], new Integer(server[1]).intValue()));
                int start = s.indexOf('|') + 1;
                s = s.substring(start);
            }
            serverIndex = sl.size();
            log.info("new Server List received, "+ sl.size() + " servers.");
        }
        nextServer();
        
    }
}
