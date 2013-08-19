/**
 * 
 */
package com.terrapin.emwin.storm;

import java.util.Map;

import org.apache.commons.lang.SerializationUtils;

import com.terrapin.emwin.object.Packet;

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
public class EMWINCatcherBolt extends BaseRichBolt {

    /**
     * 
     */
    public EMWINCatcherBolt() {
        // TODO Auto-generated constructor stub
    }

    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
            OutputCollector collector) {
        this.collector = collector;
        
    }
    
    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input) {
        Packet p = (Packet)SerializationUtils.deserialize(input.getBinaryByField("msg"));
        collector.emit(new Values(p, p.ft));
        collector.ack(input);
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("packet", "type"));
    }

}
