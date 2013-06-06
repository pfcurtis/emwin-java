

package com.terrapin.emwin;

import java.util.TimerTask;
import java.io.*;

public class EMWINHeartbeat extends TimerTask {

    private static final String HeartbeatMessage = "ByteBlast Client|admin@weather.terrapin.com|V1";
    private byte[] b;
    private OutputStream out;

    public EMWINHeartbeat(OutputStream p) {
        out = p;
        b = HeartbeatMessage.getBytes();
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(b[i] ^ 255);

    }

    public void run() {
        try {
            out.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
