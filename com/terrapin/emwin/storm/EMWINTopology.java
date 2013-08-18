package com.terrapin.emwin.storm;

import java.io.InputStream;
import java.util.Properties;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.Resources;
import com.terrapin.emwin.EMWINProperties;

public class EMWINTopology {

    public static final Logger log = LoggerFactory
            .getLogger(EMWINTopology.class);

    public static void main(String[] args) throws AlreadyAliveException,
            InvalidTopologyException, InterruptedException {

        Properties props = EMWINProperties.loadProperties();
        Boolean remote = Boolean.parseBoolean(props.getProperty("remote"));
        log.info("Building Topology");

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("emwin_spout", new EMWINSpout("emwin-spout"), 1);

        tb.setBolt("emwin_sort", new SortBolt(), 2).shuffleGrouping(
                "emwin_spout");

        tb.setBolt("text_assemble", new AssembleTextPacketsBolt(), 1)
                .shuffleGrouping("emwin_sort", "text");

        tb.setBolt("zis_assemble", new AssembleBinaryPacketsBolt(), 1)
        .shuffleGrouping("emwin_sort", "zis");

        tb.setBolt("text_parse", new ParseTextItem(), 2)
                .shuffleGrouping("text_assemble", "text_item")
                .shuffleGrouping("emwin_sort", "text_item")
                .shuffleGrouping("decompress_zis", "text_item");
        
        tb.setBolt("vtec_json", new vtecPostJSON(), 2)
                .shuffleGrouping("text_parse", "vtec_item");

        tb.setBolt("decompress_zis", new DecompressZisTextItem(), 2)
                .shuffleGrouping("zis_assemble", "zis_item");
        
        if (remote) {
            tb.setBolt("text_file_post", new HttpPostTextItem(), 2)
            .shuffleGrouping("text_parse", "text_item");
        } else {
            tb.setBolt("file_write", new WriteItemToFile(), 2)
            .shuffleGrouping("text_parse", "text_item")
            .shuffleGrouping("decompress_zis", "text_item");
        }

        Config conf = new Config();
        conf.setDebug(false);
        if (remote) {
            log.info("Sleeping 1 seconds before submitting topology");
            Thread.sleep(1000);
            StormSubmitter.submitTopology("EMWIN", conf,
                    tb.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("EMWIN_local", conf,
                    tb.createTopology());
        }
        log.info("Building Topology ... Done");
    }
}
