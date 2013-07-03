package com.terrapin.emwin;

import java.util.regex.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.PacketException;
import com.terrapin.emwin.storm.EMWINSpout;

/**
 * This class checks the header information transmitted from the data source the
 * packet header is in two formats:
 * 
 * <pre>
 * /PFFWFSGFMO.ZIS/PN 3    /PT 3    /CS 100468/FD6/3/2013 6:56:45 PM    /DL0881
 * /PFG08HURUS.JPG/PN 53   /PT 75   /CS 125691/FD6/3/2013 12:13:01 PM
 * </pre>
 * 
 * This class uses a regular expression to extract the information from the
 * header to populate the fields in the Packet class
 * 
 * @see Packet
 */

public class EMWINValidator {

    private final Logger log = LoggerFactory.getLogger(EMWINValidator.class);
    
    private static final String HEADER_REGEX = "/PF([A-Z0-9]{8})\\.([A-Z0-9]{3})/PN\\s+(\\d+)\\s+/PT\\s+(\\d+)\\s+/CS (\\d+).*/FD(\\d+\\/\\d+\\/\\d{4} \\d+:\\d+:\\d+ [A|P]M).*";
    private static final String V2_HEADER_REGEX = "/PF([A-Z0-9]{8})\\.([A-Z0-9]{3})/PN\\s+(\\d+)\\s+/PT\\s+(\\d+)\\s+/CS (\\d+).*/FD(\\d+\\/\\d+\\/\\d{4} \\d+:\\d+:\\d+ [A|P]M)\\s+/DL(\\d{4}).*";
    private Pattern hdr;
    private Pattern v2_hdr;
    private Matcher m;

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a z";
    private SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

    public EMWINValidator() {
        hdr = Pattern.compile(HEADER_REGEX);
        v2_hdr = Pattern.compile(V2_HEADER_REGEX);
    }

    /**
     * This method reads the header from an EMWINPacket and validates it. If the
     * regular expression matches, the EMWINPacket fields are populated
     * 
     * @see Packet
     * @param p
     *            Packet to be checked
     * @return true, if packet header is valid
     * @throws PacketException
     * @throws ParseException
     */
    public boolean checkHeader(Packet p) throws PacketException, ParseException {
        // First test the version 2 header
        m = v2_hdr.matcher(p.getHeader());
        if (m.matches()) {
            p.headerValid(true);            
            p.fn = m.group(1);
            p.ft = m.group(2);
            p.pn = Integer.parseInt(m.group(3));
            p.pt = Integer.parseInt(m.group(4));
            p.cs = Integer.parseInt(m.group(5));
            p.fd = df.parse(m.group(6) + " GMT");
            p.dl = Integer.parseInt(m.group(7));
            return true;
        }
        
        m = hdr.matcher(p.getHeader());
        if (m.matches()) {
            p.headerValid(true);
            p.fn = m.group(1);
            p.ft = m.group(2);
            p.pn = Integer.parseInt(m.group(3));
            p.pt = Integer.parseInt(m.group(4));
            p.cs = Integer.parseInt(m.group(5));
            p.fd = df.parse(m.group(6) + " GMT");
            p.dl = 1024;
            return true;
        }
        return false;
    }

    /**
     * Calculate the packet body checksum. Used to verify the data in the packet
     * as being valid. This value is compared to the checksum transmitted from
     * the data source
     * 
     * @see Packet
     * @param p
     *            packet to calculate the checksum
     * @return the checksum value
     */
    public long calculateChecksum(Packet p) {
        long cksum = 0;
        byte[] b = p.getBody();
        for (int i = 0; i < b.length; i++)
            cksum += (b[i] & 0xFF);
        return cksum;
    }
}
