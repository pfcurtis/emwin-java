
package com.terrapin.emwin;

import java.util.regex.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class EMWINValidator {

    /*
     * /PFFWFSGFMO.ZIS/PN 3    /PT 3    /CS 100468/FD6/3/2013 6:56:45 PM    /DL0881
     * /PFG08HURUS.JPG/PN 53   /PT 75   /CS 125691/FD6/3/2013 12:13:01 PM
    */
    private static final String HEADER_REGEX = "/PF([A-Z0-9]{8})\\.([A-Z0-9]{3})/PN\\s+(\\d+)\\s+/PT\\s+(\\d+)\\s+/CS (\\d+).*/FD(\\d+\\/\\d+\\/\\d{4} \\d+:\\d+:\\d+ [A|P]M).*";
    private Pattern hdr;
    private Matcher m;

    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a z";
    private SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

    public EMWINValidator() {
        hdr = Pattern.compile(HEADER_REGEX);
    }

    public boolean checkHeader(EMWINPacket p) throws EMWINPacketException, ParseException {
        m = hdr.matcher(p.getHeader());
        if (m.matches()) {
            p.fn = m.group(1);
            p.ft = m.group(2);
            p.pn = Integer.parseInt(m.group(3));
            p.pt = Integer.parseInt(m.group(4));
            p.cs = Integer.parseInt(m.group(5));
            p.fd = df.parse(m.group(6) + " GMT");
            return true;
        } else
            return false;
    }
}
