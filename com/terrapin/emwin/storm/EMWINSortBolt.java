

package com.terrapin.emwin.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.TopologyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import com.terrapin.emwin.EMWINPacket;

public class EMWINSortBolt extends BaseBasicBolt implements IBasicBolt {

    public final Logger log = LoggerFactory.getLogger(EMWINSortBolt.class);
    private String ptype;
    private EMWINPacket p;
    private TopologyContext tc;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        tc = context;
//      log.info("Component ID: "+tc.getThisComponentId());
//      log.info("Task ID: " + tc.getThisTaskId());
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        ptype = tuple.getValueByField("type");
        p = (EMWINPacket)tuple.getValueByField("packet");

        switch (ptype) {
        case "TXT":
            collector.emit("text", tuple, new Values(p));
            break;
        case "ZIS":
            collector.emit("zis", tuple, new Values(p));
            break;
        case "JPG":
        case "GIF":
            collector.emit("image", tuple, new Values(p));
            break;
        default:
            collector.emit("unknown", tuple, new Values(p));
            break;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
        declarer.declareStream("text", new Fields("packet"));
        declarer.declareStream("image", new Fields("packet"));
        declarer.declareStream("zis", new Fields("packet"));
        declarer.declareStream("unknown", new Fields("packet"));
    }

}
