

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

import java.util.HashMap;
import java.util.Map;

import com.terrapin.emwin.EMWINPacket;

public class EMWINSortBolt extends BaseBasicBolt {

    public final Logger log = LoggerFactory.getLogger(EMWINSortBolt.class);
    private String ptype;
    private EMWINPacket p;
    private TopologyContext tc;

    public void prepare(Map stormConf, TopologyContext context) {
        tc = context;
//      log.info("Component ID: "+tc.getThisComponentId());
//      log.info("Task ID: " + tc.getThisTaskId());
    }

    public void execute(Tuple tuple, BasicOutputCollector collector) {
        ptype = (String)tuple.getValueByField("type");
        p = (EMWINPacket)tuple.getValueByField("packet");

        switch (ptype) {
        case "TXT":
            collector.emit("text", new Values(p));
            break;
        case "ZIS":
            collector.emit("zis", new Values(p));
            break;
        case "JPG":
        case "GIF":
            collector.emit("image", new Values(p));
            break;
        default:
            collector.emit("unknown", new Values(p));
            break;
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        ofd.declareStream("text", new Fields("packet"));
        ofd.declareStream("image", new Fields("packet"));
        ofd.declareStream("zis", new Fields("packet"));
        ofd.declareStream("unknown", new Fields("packet"));
    }

}
