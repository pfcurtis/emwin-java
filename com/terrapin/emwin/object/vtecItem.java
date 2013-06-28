/**
 * 
 */
package com.terrapin.emwin.object;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.terrapin.emwin.util.VTEC;

/**
 * The VTEC item conveys severe weather conditions: watches, warnings, etc. The
 * format is:
 * 
 * <pre>
 * /O.NEW.KPHI.SV.W.0050.130626T2329Z-130627T0015Z/
 * </pre>
 * 
 * It is documented here:
 * http://www.nws.noaa.gov/os/vtec/pdfs/VTEC_explanation6.pdf
 * 
 * @author pcurtis
 * 
 */
public class vtecItem implements Serializable {

    private ArrayList<Zone> zones = null;
    private String action = null;
    private String phenonema = null;
    private String significance = null;
    private String beginString = null;
    private String endString = null;
    private String vtecKey = null;

    // 130626T2329Z-130627T0015Z
    private Date begin = null;
    private Date end = null;
    private static SimpleDateFormat df = new SimpleDateFormat("yyMMdd'T'HHmmX");

    /**
	 * 
	 */
    public vtecItem() {
        // TODO Auto-generated constructor stub
    }

    public String getVtecKey() {
        return vtecKey;
    }

    /**
	 * 
	 */
    public void setVtecKey(String vtec) {
        this.vtecKey = vtec;
        String[] v = vtec.split("\\.");
        this.setPhenonema(v[1]);
        this.setSignificance(v[2]);
    }

    /**
     * @return the begin
     */
    public String getBegin() {
        return beginString;
    }

    /**
     * @param begin
     *            the begin to set
     */
    public void setBegin(String begin) {
        this.beginString = begin;
        try {
            this.begin = df.parse(begin);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return the end
     */
    public String getEnd() {
        return endString;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(String end) {
        this.endString = end;
        try {
            this.end = df.parse(end);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @return the begin time as a Date
     */
    public Date getBeginDate() {
        return begin;
    }

    /**
     * @return the end time as a Date
     */
    public Date getEndDate() {
        return end;
    }

    /**
     * @return the zones
     */
    public ArrayList<Zone> getZones() {
        return zones;
    }

    /**
     * @param zones
     *            the zones to set
     */
    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the phenonema
     */
    public String getPhenonema() {
        return phenonema;
    }

    /**
     * @param phenonema
     *            the phenonema to set
     */
    public void setPhenonema(String phenonema) {
        this.phenonema = phenonema;
    }

    /**
     * @return the significance
     */
    public String getSignificance() {
        return significance;
    }

    /**
     * @param significance
     *            the significance to set
     */
    public void setSignificance(String significance) {
        this.significance = significance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Iterator itr = this.zones.iterator();
        StringBuffer zoneString = new StringBuffer();
        while (itr.hasNext())
            zoneString.append("  " + itr.next().toString());

        return VTEC.action.get(action) + " " + VTEC.phenomena.get(phenonema)
                + " " + VTEC.significance.get(significance) + " until " + end
                + "\n" + zoneString + "\n";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result
                + ((phenonema == null) ? 0 : phenonema.hashCode());
        result = prime * result
                + ((significance == null) ? 0 : significance.hashCode());
        result = prime * result + ((zones == null) ? 0 : zones.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        vtecItem other = (vtecItem) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (phenonema == null) {
            if (other.phenonema != null)
                return false;
        } else if (!phenonema.equals(other.phenonema))
            return false;
        if (significance == null) {
            if (other.significance != null)
                return false;
        } else if (!significance.equals(other.significance))
            return false;
        if (zones == null) {
            if (other.zones != null)
                return false;
        } else if (!zones.equals(other.zones))
            return false;
        return true;
    }

}
