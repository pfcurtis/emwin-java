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

import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.vtecItem;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

/**
 * @author pcurtis
 *
 */
public class WriteTextItemToFile extends BaseBasicBolt {
    private TextItem t;
    public static final Logger log = LoggerFactory.getLogger(WriteTextItemToFile.class);
    private Properties props;
    
    public WriteTextItemToFile() {
        props = EMWINTopology.loadProperties();
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        t = (TextItem) input.getValueByField("item");
        int hash = t.hashCode();
        try {
            File file = new File(props.getProperty("json.directory") + "/" + t.getPacketFileName() +"." + t.getPacketFileType());

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(t.getBody());
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
