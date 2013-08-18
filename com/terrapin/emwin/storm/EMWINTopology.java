package com.terrapin.emwin.storm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

import com.mapr.ProtoSpout;
import com.terrapin.emwin.EMWINProperties;

public class EMWINTopology {

    public static final Logger log = LoggerFactory
            .getLogger(EMWINTopology.class);

    public static void main(String[] args) throws AlreadyAliveException,
            InvalidTopologyException, InterruptedException, IOException {

        Properties props = EMWINProperties.loadProperties();
        Boolean remote = Boolean.parseBoolean(props.getProperty("remote"));
        String franzBaseDir = props.getProperty("franz.basedir", "/mapr");
        String franzTopic = props.getProperty("franz.topic", "emwin");
        
        // init the MapR Tail Spout
        BlobTupleParser tp = new BlobTupleParser();
        File statusFile = new File(franzBaseDir + "/" + franzTopic + "/status");
        File inDir = new File(franzBaseDir + "/" + franzTopic);
        Pattern inPattern = Pattern.compile("0.*");
        ProtoSpout spout = new ProtoSpout(tp, statusFile, inDir, inPattern);

        log.info("Building Topology");

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("tail_spout", spout, 1);
        tb.setBolt("emwin_spout", new EMWINCatcherBolt(), 1).shuffleGrouping("tail_spout");

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

        tb.setBolt("vtec_json", new vtecPostJSON(), 2).shuffleGrouping(
                "text_parse", "vtec_item");

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
            StormSubmitter.submitTopology("EMWIN", conf, tb.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("EMWIN_local", conf, tb.createTopology());
        }
        log.info("Building Topology ... Done");
    }
}
