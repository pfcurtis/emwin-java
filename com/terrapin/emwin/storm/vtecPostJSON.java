/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.EMWINProperties;
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
        props = EMWINProperties.loadProperties();
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        v = (vtecItem) input.getValueByField("item");
        int hash = v.hashCode();
        try {
            File file = new File(props.getProperty("json.directory") + "/" + v.getVtecKey() + "-" + hash + ".json");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            JSONObject j = new JSONObject(v);
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(j.toString());
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

}
