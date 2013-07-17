package com.terrapin.emwin.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import backtype.storm.task.TopologyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.TextItem;

public class SortBolt extends BaseBasicBolt {

    public final Logger log = LoggerFactory.getLogger(SortBolt.class);
    private String ptype;
    private Packet p;
    private TopologyContext tc;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        tc = context;
        // log.info("Component ID: "+tc.getThisComponentId());
        // log.info("Task ID: " + tc.getThisTaskId());
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        ptype = (String) tuple.getValueByField("type");
        p = (Packet) tuple.getValueByField("packet");

        switch (ptype) {
        case "TXT":
            if ((p.pn == 1) && (p.pt == 1)) {
                try {
                    collector.emit("text_item", new Values(new TextItem(p)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                collector.emit("text", new Values(p));
            }
            break;
        case "ZIS":
            collector.emit("zis", new Values(p));
            break;
        case "JPG":
        case "GIF":
            // collector.emit("image", new Values(p));
            break;
        default:
            // collector.emit("unknown", new Values(p));
            break;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declareStream("text_item", new Fields("item"));
        ofd.declareStream("text", new Fields("packet"));
        ofd.declareStream("image", new Fields("packet"));
        ofd.declareStream("zis", new Fields("packet"));
        ofd.declareStream("unknown", new Fields("packet"));
    }

}
