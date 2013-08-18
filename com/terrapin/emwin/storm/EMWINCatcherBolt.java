/**
 * 
 */
package com.terrapin.emwin.storm;

import org.apache.commons.lang.SerializationUtils;

import com.terrapin.emwin.object.Packet;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * @author pcurtis
 *
 */
public class EMWINCatcherBolt extends BaseBasicBolt {

    /**
     * 
     */
    public EMWINCatcherBolt() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        Packet p = (Packet)SerializationUtils.deserialize(input.getBinaryByField("msg"));
        collector.emit(new Values(p, p.ft));
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("packet", "type"));
    }

}
