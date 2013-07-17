/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        log.info("z.getBody().length = " + z.getBody().length);
        try {
            IOUtils.copy(new ZipInputStream(new ByteArrayInputStream(z.getBody())), out);
            TextItem t = new TextItem();
            t.setPacketFileName(z.getPacketFileName());
            t.setPacketFileType(z.getPacketFileType());
            t.setPacketDate(z.getPacketDate());
            log.info("t.body().length = " + out.size());
            t.setBody(out.toString("ISO-8859-1"));
            collector.emit("text_item", new Values(t));
        } catch(Exception e) {
            log.error("ZIS", e);
        }

        
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("text_item", new Fields("item"));
    }

}