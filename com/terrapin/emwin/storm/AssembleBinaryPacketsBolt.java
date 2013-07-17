/**
 * 
 */
package com.terrapin.emwin.storm;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.ZisItem;

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
 * @author pcurtis
 *
 */
public class AssembleBinaryPacketsBolt extends BaseBasicBolt {

    public final Logger log = LoggerFactory
            .getLogger(AssembleBinaryPacketsBolt.class);
    private HashMap<String, byte[]> pkts = new HashMap<String, byte[]>();
    private Packet p;


    /* (non-Javadoc)
     * @see backtype.storm.task.IBolt#execute(backtype.storm.tuple.Tuple)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        p = (Packet) input.getValueByField("packet");
        String pktKey = p.fn + p.fd.getTime();

        if (p.pn == 1) {
            pkts.put(pktKey, p.getBody());
            log.debug("Added " + p.fn + "." + p.ft + " ...");
        }

        if ((p.pn > 1) && (pkts.containsKey(p.fn + p.fd.getTime()))) {
            byte[] s = pkts.get(pktKey);
            byte[] n = new byte[s.length + 1024];
            System.arraycopy(s, 0, n, 0, s.length);
            System.arraycopy(p.getBody(), 0, n, s.length, 1024);            
            pkts.put(pktKey, n);
            log.debug("Updated " + p.fn + "." + p.ft + " [" + p.pn + "/" + p.pt
                    + "] ...");
        }

        if ((p.pn == p.pt) && (pkts.containsKey(pktKey))) {
            ZisItem t = new ZisItem();
            t.setPacketFileName(p.fn);
            t.setPacketFileType(p.ft);
            t.setPacketDate(p.fd);
            t.setBody(pkts.get(pktKey));
            collector.emit("text_item", new Values(t));
            log.debug("Assembled " + p.fn + "." + p.ft + " ...");
        }

    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("zis_item", new Fields("item"));
    }
    
}
