

package com.terrapin.emwin.storm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.*;

public class EMWINTopology {

    public static final Logger log = LoggerFactory.getLogger(EMWINTopology.class);
    public static final boolean remote = false;

    public static void main(String[] args) throws AlreadyAliveException,
        InvalidTopologyException, InterruptedException {

        log.info("Building Topology");

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("emwin_spout", new EMWINSpout(), 1);
        tb.setBolt("emwin_print_header", new EMWINPrintHeaderBolt(), 2).shuffleGrouping("emwin_spout");

        Config conf = new Config();
        conf.setDebug(true);
        if (remote) {
            log.info("Sleeping 1 seconds before submitting topology");
            Thread.sleep(1000);
            StormSubmitter.submitTopology("EMWIN topology",
                                          conf, tb.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("EMWIN local topology",
                                   conf, tb.createTopology());
        }
        log.info("Building Topology ... Done");
    }
}
