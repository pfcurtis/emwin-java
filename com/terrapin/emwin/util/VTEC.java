/**
 * Static Lookup Hash tables with the VTEC codes
 */
package com.terrapin.emwin.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pcurtis
 * 
 */
public final class VTEC {

    public static final Map<String, String> significance = new HashMap<String, String>() {
        {
            put("W", "Warning");
            put("F", "Forecast");
            put("A", "Watch");
            put("O", "Outlook");
            put("Y", "Advisory");
            put("N", "Synopsis");
            put("S", "Statement");
        }
    };

    public static final Map<String, String> action = new HashMap<String, String>() {
        {
            put("NEW", "New");
            put("CON", "Continued");
            put("EXT", "Extended (time)");
            put("EXA", "Extended (area)");
            put("EXB", "Extended (both time and area)");
            put("UPG", "Upgraded");
            put("CAN", "Cancelled");
            put("EXP", "Expired");
            put("COR", "Correction");
            put("ROU", "Routine");
        }
    };

    public static final Map<String, String> phenomena = new HashMap<String, String>() {
        {
            put("AF", "Ashfall");
            put("AS", "Air Stagnation");
            put("BS", "Blowing Snow");
            put("BW", "Brisk Wind");
            put("BZ", "Blizzard");
            put("CF", "Coastal Flood");
            put("DS", "Dust Storm");
            put("DU", "Blowing Dust");
            put("EC", "Extreme Cold ");
            put("EH", "Excessive Heat");
            put("EW", "Extreme Wind");
            put("FA", "Areal Flood");
            put("FF", "Flash Flood");
            put("FG", "Dense Fog");
            put("FL", "Flood");
            put("FR", "Frost");
            put("FW", "Fire Weather");
            put("FZ", "Freeze ");
            put("GL", "Gale");
            put("HF", "Hurricane Force Wind");
            put("HI", "Inland Hurricane");
            put("HS", "Heavy Snow");
            put("HT", "Heat ");
            put("HU", "Hurricane");
            put("HW", "High Wind");
            put("HY", "Hydrologic");
            put("HZ", "Hard Freeze");
            put("IP", "Sleet");
            put("IS", "Ice Storm");
            put("LB", "Lake Effect Snow and Blowing Snow");
            put("LE", "Lake Effect Snow");
            put("LO", "Low Water");
            put("LS", "Lakeshore Flood");
            put("LW", "Lake Wind");
            put("MA", "Marine");
            put("RB", "Small Craft for Rough Bar");
            put("SB", "Snow and Blowing Snow");
            put("SC", "Small Craft");
            put("SE", "Hazardous Seas");
            put("SI", "Small Craft for Winds");
            put("SM", "Dense Smoke");
            put("SN", "Snow");
            put("SR", "Storm");
            put("SU", "High Surf");
            put("SV", "Severe Thunderstorm");
            put("SW", "Small Craft for Hazardous Seas");
            put("TI", "Inland Tropical Storm");
            put("TO", "Tornado");
            put("TR", "Tropical Storm");
            put("TS", "Tsunami");
            put("TY", "Typhoon");
            put("UP", "Ice Accretion");
            put("WC", "Wind Chill ");
            put("WI", "Wind");
            put("WS", "Winter Storm");
            put("WW", "Winter Weather");
            put("ZF", "Freezing Fog");
            put("ZR", "Freezing Rain");
        }
    };
}
