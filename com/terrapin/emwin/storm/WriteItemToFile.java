/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.EMWINProperties;
import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.ZisItem;
import com.terrapin.emwin.object.vtecItem;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * @author pcurtis
 *
 */
public class WriteItemToFile extends BaseRichBolt {
    private Object obj;
    private String fn;
    private String ft;
    private byte[] pBody;
    private static final Logger log = LoggerFactory.getLogger(WriteItemToFile.class);
    private Properties props;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context,
            OutputCollector collector) {
        this.collector = collector;
        
    }
    
    public WriteItemToFile() {
        props = EMWINProperties.loadProperties();
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input) {
        obj = input.getValueByField("item");
        
        if (obj instanceof TextItem) {
            TextItem t = (TextItem) obj;
            fn = t.getPacketFileName();
            ft = t.getPacketFileType();
            try {
                pBody = t.getBody().getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (obj instanceof ZisItem) {
            ZisItem t = (ZisItem) obj;
            fn = t.getPacketFileName();
            ft = t.getPacketFileType();
            pBody = t.getBody();
        } else
            return;
        
        int hash = obj.hashCode();
        try {
            File file = new File(props.getProperty("json.directory") + "/" + fn +"." + ft);

            // if file does not exist, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile());
            fw.write(pBody);
            fw.close();
            collector.ack(input);
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
