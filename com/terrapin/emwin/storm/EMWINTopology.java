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

public class EMWINTopology {

    public static final Logger log = LoggerFactory
            .getLogger(EMWINTopology.class);

    public static Properties loadProperties() {
        Properties props = new Properties();
        loadProperties("emwin-java.properties", props);
        return props;
    }

    private static Properties loadProperties(String resource, Properties props) {
        try {
            InputStream is = Resources.getResource(resource).openStream();
            log.info("Loading properties from '" + resource + "'.");
            props.load(is);
        } catch (Exception e) {
            log.info("Not loading properties from '" + resource + "'.");
            log.info(e.getMessage());
        }
        return props;
    }

    public static void main(String[] args) throws AlreadyAliveException,
            InvalidTopologyException, InterruptedException {

        Properties props = EMWINTopology.loadProperties();
        Boolean remote = Boolean.parseBoolean(props.getProperty("remote"));
        log.info("Building Topology");

        TopologyBuilder tb = new TopologyBuilder();

        tb.setSpout("emwin_spout", new EMWINSpout(), 1);

        tb.setBolt("emwin_sort", new SortBolt(), 2).shuffleGrouping(
                "emwin_spout");

        tb.setBolt("text_assemble", new AssembleTextPacketsBolt(), 1)
                .shuffleGrouping("emwin_sort", "text");

        tb.setBolt("text_parse", new ParseTextItem(), 2)
                .shuffleGrouping("text_assemble", "text_item")
                .shuffleGrouping("emwin_sort", "text_item");

        Config conf = new Config();
        conf.setDebug(true);
        if (remote) {
            log.info("Sleeping 1 seconds before submitting topology");
            Thread.sleep(1000);
            StormSubmitter.submitTopology("EMWIN topology", conf,
                    tb.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("EMWIN local topology", conf,
                    tb.createTopology());
        }
        log.info("Building Topology ... Done");
    }
}
