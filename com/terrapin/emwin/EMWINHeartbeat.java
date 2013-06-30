package com.terrapin.emwin;

import java.util.TimerTask;
import java.io.*;

/**
 * This class sends the "heartbeat" back to the EMWIN data source indicating the
 * stream is still in use and the client is still alive. Utilize a TimerTask to
 * automatically send the data back. The class also "encodes" the data with by
 * XORing it
 * 
 * @author pcurtis
 * 
 */
public class EMWINHeartbeat extends TimerTask {

    private static final String HeartbeatMessage = "ByteBlast Client|admin@weather.terrapin.com|V1";
    private byte[] b;
    private OutputStream out;

    /**
     * Create a heartbeat
     * 
     * @param p
     *            the output stream connected to the data source
     */
    public EMWINHeartbeat(OutputStream p) {
        out = p;
        b = HeartbeatMessage.getBytes();
        for (int i = 0; i < b.length; i++)
            b[i] = (byte) (b[i] ^ 255);

    }

    /**
     * TimeTask run method
     */
    @Override
    public void run() {
        try {
            out.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
