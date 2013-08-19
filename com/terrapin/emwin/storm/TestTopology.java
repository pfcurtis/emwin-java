package com.terrapin.emwin.storm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

import com.mapr.ProtoSpout;
import com.terrapin.emwin.EMWINProperties;

public class TestTopology {

    public static final Logger log = LoggerFactory
            .getLogger(EMWINTopology.class);

    public static void main(String[] args) throws AlreadyAliveException,
            InvalidTopologyException, InterruptedException, IOException {

        Properties props = EMWINProperties.loadProperties();
        String franzBaseDir = props.getProperty("franz.basedir", "/mapr");
        String franzTopic = props.getProperty("franz.topic", "emwin");
        
        // init the MapR Tail Spout
        BlobTupleParser tp = new BlobTupleParser();
        File statusFile = new File(franzBaseDir + "/" + franzTopic + "/status");
        File inDir = new File(franzBaseDir + "/" + franzTopic);
        Pattern inPattern = Pattern.compile("0.*");
        ProtoSpout spout = new ProtoSpout(tp, statusFile, inDir, inPattern);

        // TODO this should be set to true, but somebody isn't acking tuples correctly and that causes hangs
        spout.setReliableMode(false);

        log.info("Building Topology");

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("tail_spout", spout, 1);
        tb.setBolt("emwin_spout", new EMWINCatcherBolt(), 1).shuffleGrouping("tail_spout");
        tb.setBolt("print_header", new PrintHeaderBolt(), 1).shuffleGrouping("emwin_spout");

        Config conf = new Config();
        conf.setDebug(false);
        log.info("Sleeping 1 seconds before submitting topology");
        Thread.sleep(1000);
        StormSubmitter.submitTopology("Test", conf, tb.createTopology());
        log.info("Building Topology ... Done");
    }
}
