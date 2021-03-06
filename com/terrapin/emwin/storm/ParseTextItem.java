package com.terrapin.emwin.storm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.Zone;
import com.terrapin.emwin.object.vtecItem;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class ParseTextItem extends BaseRichBolt {

    public final Logger log = LoggerFactory.getLogger(ParseTextItem.class);
    
    private SimpleDateFormat df = new SimpleDateFormat("ddHHmm");
    
    private static final String ISSUER_REGEX = "(\\w{4}\\w*)\\s+([B|R|N|T|P|K|C]\\w{3})\\s+(\\d{6})";
    private static final String DATE_REGEX = "(\\d{1,2})(\\d{2}) ([A|P]M) ([A-Z][A-Z][A-Z]) [A-Z][A-Z][A-Z] ([A-Z][A-Z][A-Z]) ([0-9]+) ([0-9]+)";
    private static final String NEW_STATE = "([A-Z][A-Z])([C|Z])(\\d{3}|ALL)-(.*)";
    private static final String EXPIRE_DATE = "(\\d{6})";
    private static final String STATE = "([A-Z][A-Z])([C|Z])(\\d{3}|ALL)";
    private static final String VTEC_REGEX = "\\/O\\.(\\w{3})\\.(\\w{4}\\.\\w{2}\\.\\w\\.\\w{4})\\.(\\w{12})\\-(\\w{12})\\/";

    private Pattern issuer;
    private Pattern nwsDate;
    private Pattern newState;
    private Pattern expires;
    private Pattern state;
    private Pattern vtec;

    private Matcher m;
    private TextItem t;

    private ArrayList<Zone> zlist;
    private OutputCollector collector;

    
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        issuer = Pattern.compile(ISSUER_REGEX);
        nwsDate = Pattern.compile(DATE_REGEX);
        newState = Pattern.compile(NEW_STATE);
        expires = Pattern.compile(EXPIRE_DATE);
        state = Pattern.compile(STATE);
        vtec = Pattern.compile(VTEC_REGEX);
        this.collector = collector;
        log.info("compiled regexes");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("parsed_text_item", new Fields("item"));
        declarer.declareStream("vtec_item", new Fields("item"));
    }

    @Override
    public void execute(Tuple input) {
        // TODO Auto-generated method stub
        t = (TextItem) input.getValueByField("item");
        log.debug(t.getPacketFileName() + "." + t.getPacketFileType() + " "
                + t.getPacketDate().getTime());

        Scanner scanner = new Scanner(t.getBody());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // process the line
            if (line.indexOf("$$") != -1)
                log.debug("New Text Message detected.");

            m = issuer.matcher(line);
            if (m.matches()) {
                log.debug("mtype=" + m.group(1) + "  wid=" + m.group(2)
                        + "  date=" + m.group(3));
                t.setMtype(m.group(1));
                t.setWid(m.group(2));
            }

            m = nwsDate.matcher(line);
            if (m.matches()) {
                log.debug(m.group(1) + m.group(2) + " " + m.group(3));
            }

            m = vtec.matcher(line);
            if (m.matches()) {
                vtecItem v = new vtecItem();
                log.debug("VTEC Key = " + m.group(2));
                v.setAction(m.group(1));
                v.setVtecKey(m.group(2));
                v.setBegin(m.group(3));
                v.setEnd(m.group(4));
                v.setZones(zlist);
                collector.emit("vtec_item", new Values(v));
            }

            m = newState.matcher(line);
            if (m.matches()) {
                zlist = new ArrayList<Zone>();
                boolean notSeenExpire = true;
                log.debug("st=" + m.group(1) + "  z=" + m.group(2) + "  c="
                        + m.group(3));
                String st = m.group(1);
                String zcode = m.group(2);

                Zone z = new Zone(m.group(1), m.group(2), m.group(3));
                zlist.add(z);

                String[] sscan = m.group(4).split("-");

                while (notSeenExpire) {
                    for (int x = 0; x < sscan.length; x++) {
                        log.debug("  c=" + sscan[x]);

                        // range of zones == "053>061"
                        if (sscan[x].indexOf(">") != -1) {
                            parseRangeToken(zlist, sscan[x], st, zcode);
                            continue;
                        }

                        // Change of the state SSCNNNN
                        Matcher stateRE = state.matcher(sscan[x]);
                        if (stateRE.matches()) {
                            st = stateRE.group(1);
                            zcode = stateRE.group(2);
                            zlist.add(new Zone(st, zcode, stateRE.group(3)));
                            continue;
                        }

                        // yymmdd-
                        Matcher expireRE = expires.matcher(sscan[x]);
                        if (expireRE.matches()) {
                            notSeenExpire = false;
                            try {
                                t.setExpires(df.parse(expireRE.group(1)));
                            } catch (ParseException e) {
                                log.warn("expiresRE: " + e.getMessage());
                            }
                            continue;
                        }

                        zlist.add(new Zone(st, zcode, sscan[x]));
                    }

                    if (notSeenExpire)
                        if (scanner.hasNextLine())
                            sscan = scanner.nextLine().split("-");
                }
                t.setZones(zlist);
                Iterator<Zone> itr = t.getZones().iterator();
                while (itr.hasNext())
                    log.debug(itr.next().toString());
            }

        } // while scanner
        log.info("text_item '"+t.getPacketFileName()+"' emitted.");
        collector.emit("parsed_text_item", new Values(t));
        collector.ack(input);
    }

    private void parseRangeToken(ArrayList<Zone> zlist, String t, String st, String zc) {
        String[] s = t.split(">");

        Matcher stateRE = state.matcher(s[0]);
        if (stateRE.matches()) {
            st = stateRE.group(1);
            zc = stateRE.group(2);
            s[0] = stateRE.group(3);
        }

        try {
            int start = Integer.valueOf(s[0]).intValue();
            int end = Integer.valueOf(s[1]).intValue();

            for (int x = start; x <= end; x++) {
                Zone z = new Zone(st, zc, Integer.toString(x));
                zlist.add(z);
            }
        } catch (java.lang.NumberFormatException e) {
            log.warn("Zone range parse: " + e.getMessage());
        }
    }

}
