

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

import com.terrapin.emwin.*;

public class EMWINTopology {

    public static void main(String[] args) throws AlreadyAliveException,
        InvalidTopologyException, InterruptedException {

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("emwin_spout", new EMWINSpout(), 1);
        tb.setBolt("emwin_print_header", new EMWINPrintHeaderBolt()).shuffleGrouping("emwin_spout");

        Config conf = new Config();
        conf.setDebug(true);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("EMWIN local topology",
                               conf, tb.createTopology());
    }
}
