/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.ZisItem;

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
public class DecompressZisTextItem extends BaseBasicBolt {
    private static final Logger log = LoggerFactory.getLogger(DecompressZisTextItem.class);

    /* (non-Javadoc)
     * @see backtype.storm.topology.IBasicBolt#execute(backtype.storm.tuple.Tuple, backtype.storm.topology.BasicOutputCollector)
     */
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        ZisItem z = (ZisItem) input.getValueByField("item");
        byte[] result = new byte[z.getBody().length * 10];
        Inflater decompresser = new Inflater();
        decompresser.setInput(z.getBody(), 0, z.getBody().length);
        log.info("z.getBody().length = " + z.getBody().length);
        try {
            int resultLength = decompresser.inflate(result);
            byte[] body = new byte[resultLength];
            body = Arrays.copyOfRange(result, 0, resultLength);
            TextItem t = new TextItem();
            t.setPacketFileName(z.getPacketFileName());
            t.setPacketFileType(z.getPacketFileType());
            t.setPacketDate(z.getPacketDate());
            t.setBody(new String(body, "ISO-8859-1"));
            collector.emit("text_item", new Values(t));
        } catch (DataFormatException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        decompresser.end();

        
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("text_item", new Fields("item"));
    }

}
