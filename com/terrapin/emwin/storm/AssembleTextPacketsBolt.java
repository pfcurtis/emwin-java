/**
 *
 */
package com.terrapin.emwin.storm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.TextItem;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This bolt assembles multiple packet text items into a single TextItem, then
 * emits them
 * 
 * @author pcurtis
 * 
 */
public class AssembleTextPacketsBolt extends BaseRichBolt {

    /*
     * (non-Javadoc)
     * 
     * @see
     * backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple,
     * backtype.storm.topology.BasicOutputCollector)
     */

    public final Logger log = LoggerFactory
            .getLogger(AssembleTextPacketsBolt.class);
    private HashMap<String, StringBuffer> pkts = new HashMap<String, StringBuffer>();
    private Packet p;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
            OutputCollector collector) {
        this.collector = collector;
        
    }

    @Override
    public void execute(Tuple input) {
        p = (Packet) input.getValueByField("packet");
        String pktKey = p.fn + p.fd.getTime();

        if (p.pn == 1) {
            StringBuffer s = new StringBuffer(new String(p.getBody()));
            pkts.put(pktKey, s);
            log.debug("Added " + p.fn + "." + p.ft + " ...");
            return;
        }

        if ((p.pn > 1) && (pkts.containsKey(p.fn + p.fd.getTime()))) {
            StringBuffer s = (StringBuffer) pkts.get(pktKey);
            s.append(new String(p.getBody()));
            pkts.put(pktKey, s);
            log.debug("Updated " + p.fn + "." + p.ft + " [" + p.pn + "/" + p.pt
                    + "] ...");
        }

        if ((p.pn == p.pt) && (pkts.containsKey(pktKey))) {
            TextItem t = new TextItem();
            t.setPacketFileName(p.fn);
            t.setPacketFileType(p.ft);
            t.setPacketDate(p.fd);
            t.setBody(pkts.get(pktKey).toString());
            collector.emit("text_item", new Values(t));
            log.debug("Assembled " + p.fn + "." + p.ft + " ...");
            pkts.remove(pktKey);
        }
        collector.ack(input);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * backtype.storm.topology.IComponent#declareOutputFields(backtype.storm
     * .topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("text_item", new Fields("item"));
    }

}
