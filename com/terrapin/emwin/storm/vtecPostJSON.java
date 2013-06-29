/**
 * 
 */
package com.terrapin.emwin.storm;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.vtecItem;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * This bolt reads the vtec_item stream, creates a JSON object from the vtecItem, and does something with it. (write to file or POST to web site)
 * 
 * @author pcurtis
 *
 */
public class vtecPostJSON extends BaseBasicBolt {
    private vtecItem v;
    public static final Logger log = LoggerFactory.getLogger(vtecPostJSON.class);
    private Properties props;
    
    /**
     * 
     */
    public vtecPostJSON() {
        props = EMWINTopology.loadProperties();
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        v = (vtecItem) input.getValueByField("item");

    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

}
