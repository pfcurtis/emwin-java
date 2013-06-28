package com.terrapin.emwin.object;

import java.io.Serializable;

public class Zone implements Serializable {

    private static final long serialVersionUID = 2L;

    public Integer state = null;
    public String code = null;
    public Integer zone = null;

    private Zone() {
    }

    public Zone(int st, String c, int z) {
       state = new Integer(st);
       code = new String(c);
       zone = new Integer(z);
    }

}
