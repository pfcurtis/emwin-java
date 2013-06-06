

package com.terrapin.emwin.storm;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import com.terrapin.emwin.EMWINPacket;

public class EMWINPrintHeaderBolt extends BaseBasicBolt {

    private EMWINPacket p;

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        p = (EMWINPacket)tuple.getValueByField("packet");
        System.out.println(p.getHeader());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
    }
    
}
