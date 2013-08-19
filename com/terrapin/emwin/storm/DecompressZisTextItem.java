/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.TextItem;
import com.terrapin.emwin.object.ZisItem;

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
public class DecompressZisTextItem extends BaseRichBolt {
    private static final Logger log = LoggerFactory.getLogger(DecompressZisTextItem.class);
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
        ZisItem z = (ZisItem) input.getValueByField("item");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        log.debug("z.getBody().length = " + z.getBody().length);
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(z.getBody()));   
        try {
            ZipEntry ze = zin.getNextEntry();
            String[] fn = ze.getName().split("\\.");
            log.debug("Zip Entry: '" + ze.getName() + "'"); 
            int rc = IOUtils.copy(zin, out);
            log.debug("out.size() = " + out.size());
            TextItem t = new TextItem();
            t.setPacketFileName(fn[0]);
            t.setPacketFileType(fn[1]);
            t.setPacketDate(z.getPacketDate());
            t.setBody(out.toString("ISO-8859-1"));
            collector.emit("uncompressed_text", new Values(t));
        } catch(Exception e) {
            log.warn("ZIS" + e.getMessage());
        }
        collector.ack(input);
    }

    /* (non-Javadoc)
     * @see backtype.storm.topology.IComponent#declareOutputFields(backtype.storm.topology.OutputFieldsDeclarer)
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("uncompressed_text", new Fields("item"));
    }

}
