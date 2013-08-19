package com.terrapin.emwin.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import com.terrapin.emwin.object.Packet;

public class PrintHeaderBolt extends BaseRichBolt {

    public final Logger log = LoggerFactory.getLogger(PrintHeaderBolt.class);
    private Packet p;
    private TopologyContext tc;
    private OutputCollector collector;


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        tc = context;
        this.collector = collector;
        // log.info("Component ID: "+tc.getThisComponentId());
        // log.info("Task ID: " + tc.getThisTaskId());
    }

    @Override
    public void execute(Tuple tuple) {
        p = (Packet) tuple.getValueByField("packet");
        if (p.isPacketValid())
            log.info("(" + tc.getThisTaskId() + ") " + p.fn + ": " + p.pn
                    + " of " + p.pt + " + " + p.fd);
        else
            log.info("(" + tc.getThisTaskId() + ") " + p.fn + ": " + p.pn
                    + " of " + p.pt + " - " + p.fd);
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
    }

}
