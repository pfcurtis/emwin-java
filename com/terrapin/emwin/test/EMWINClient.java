

package com.terrapin.emwin.test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.text.ParseException;
import com.terrapin.emwin.*;

public class EMWINClient {

    public static void main(String[] args) throws IOException, EMWINPacketException, ParseException {

        Socket echoSocket = null;
        OutputStream out = null;
        EMWINInputStream in = null;

        try {
            echoSocket = new Socket("www.opennoaaport.net", 2211);
            out = echoSocket.getOutputStream();
            in = new EMWINInputStream(echoSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection");
            System.exit(1);
        }

        Timer t = new Timer("Heartbeat", true);
        t.schedule(new EMWINHeartbeat(out), 0, 300000);

        EMWINValidator v = new EMWINValidator();
        EMWINScanner sc = new EMWINScanner(in,v);

        while (sc.hasNext()) {
            EMWINPacket p = sc.next();
            if (v.checkHeader(p))
                System.out.println(p.pn + " of " + p.pt+ ": " + p.fn + " " + p.fd + " +");
            else
                System.out.println(p.pn + " of " + p.pt+ ": " + p.fn + " " + p.fd + " -");
        }

        out.close();
        in.close();
        echoSocket.close();
    }
}

