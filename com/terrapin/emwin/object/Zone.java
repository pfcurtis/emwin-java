package com.terrapin.emwin.object;

import java.io.Serializable;

/**
 * This class represents a single county or zone that an EMWIN item affects
 *  
 * @author pcurtis
 *
 */
public class Zone implements Serializable {

    private static final long serialVersionUID = 2L;

    private Integer state = null;
    private String code = null;
    private Integer zone = null;

    private Zone() {
    }

    public Zone(int st, String c, int z) {
       state = new Integer(st);
       code = new String(c);
       zone = new Integer(z);
    }

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}

	/**
	 * @return the zone
	 */
	public Integer getZone() {
		return zone;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * @param zone the zone to set
	 */
	public void setZone(Integer zone) {
		this.zone = zone;
	}

}
