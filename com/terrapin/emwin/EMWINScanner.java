package com.terrapin.emwin;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.PacketException;
import com.terrapin.emwin.storm.EMWINSpout;
/**
 * This class examines the incoming byte stream for specific character
 * sequences. The sequences signal the start of the two types of packets
 * received: data packets and server lists. Data packets always begin with
 * "/PF" while server list packets begin with "/ServerList/"
 * <p>
 * Once the data packet start is detected, an EMWINPacket is constructed and
 * validated
 */

public class EMWINScanner implements Serializable {

    private final Logger log = LoggerFactory.getLogger(EMWINScanner.class);

    private EMWINInputStream i;
    private EMWINConnection con;
    private EMWINValidator v;
    private StringBuffer header;
    private byte[] body;
    private Packet p;
    

    @SuppressWarnings("unused")
    private EMWINScanner() { 
    }

    /**
     * 
     * @param vin
     *            an instance of the validator class
     */
    public EMWINScanner(EMWINValidator vin, EMWINConnection c) {
        v = vin;
        con = c;
        i = con.getIn();
    }
    
    /**
     * This method set the "current" input stream
     * 
     */
    public void setIn() {
        i = con.getIn();
    }
    /**
     * This method blocks until a complete packet has been received. When a full
     * packet is available, the method returns. This method would be used as the
     * test in a while() loop.
     * 
     * @return true when a packet is available
     * @throws java.io.IOException
     */
    // This method blocks until a packet is available
    public boolean hasNext() throws java.io.IOException, java.net.SocketException {
        p = new Packet(v);
        scan();
        try {
            p.setBody(body);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method returns the constructed EMWINPacket. This method would be
     * used inside a while() to retrieve the data.
     * 
     * @return complete data packet
     * @see Packet
     */
    public Packet next() {
        return p;
    }

    /*
     * We are scanning for two types of packets. A data packet starts with "/PF"
     * and the server list starts with "/ServerList"
     */
    private void scan() throws java.io.IOException, java.net.SocketException {

        int b;
        byte[] hdr = new byte[75];
        char[] hdr_c = new char[75];

        while (true) {
            b = i.readUnsignedByte();

            if (b == '/') {
                b = i.readUnsignedByte();
            } else {
                // System.out.println("read1: "+b+" '"+(char)b+"'");
                continue;
            }

            if ((b == 'P') || (b == 'S')) {
                b = i.readUnsignedByte();
            } else {
                continue;
            }

            // Data packet
            if (b == 'F') {
                header = new StringBuffer("/PF");
                i.readFully(hdr);
                for (int i = 0; i < hdr.length; i++)
                    hdr_c[i] = (char) hdr[i];
                header.append(hdr_c);
                // In order to read the correct number of bytes, we need to check the header of the packet
                try {
                    p.setHeader(header.toString());
                    log.debug("Header: "+p.fn+"."+p.ft+"   DL="+p.dl);
                } catch (Exception e) {
                    break;
                }
                
                // Remove the CR + NL characters
                i.getDataInputStream().read();
                i.getDataInputStream().read();
                body = new byte[p.dl];
                i.readFully(body);
                
                if (p.dl != 1024) { 
                    // EMWIN version 2 protocol will send less than the 1024 bytes. If this is true, the packet body needs
                    // to be decompressed 
                    Inflater decompresser = new Inflater();
                    decompresser.setInput(body, 0, p.dl);
                    byte[] result = new byte[1024];
                    try {
                        int resultLength = decompresser.inflate(result);
                        body = Arrays.copyOfRange(result, 0, resultLength);
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }
                    decompresser.end();
                    
                }
                break;
            }
            
            if (b == 'e') {
                StringBuffer serverList = new StringBuffer("Se");
                int pkt;
                char sl_c = 0;
                char last = 0;
                while (true) {
                    pkt = i.readUnsignedByte();
                    sl_c = (char)pkt;
                    if ((last == '|') && (sl_c == '\\'))
                            break;
                    serverList.append(sl_c);
                    last = sl_c;
                }
                con.setServerList(serverList.toString());
                log.debug("/ServerList/ = "+serverList);
            } else {
                continue;
            }
        }
    }
}
