package com.terrapin.emwin.storm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.contrib.signals.spout.BaseSignalSpout;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.terrapin.emwin.EMWINConnection;
import com.terrapin.emwin.EMWINHeartbeat;
import com.terrapin.emwin.EMWINInputStream;
import com.terrapin.emwin.EMWINScanner;
import com.terrapin.emwin.EMWINValidator;
import com.terrapin.emwin.object.Packet;

public class EMWINSpout extends BaseSignalSpout {
    /**
     * 
     */
    private static final long serialVersionUID = -1293760041316856250L;
    private SpoutOutputCollector _collector;
    private EMWINScanner sc;
    private EMWINValidator v;
    private EMWINConnection con = new EMWINConnection();
    
    private Random _rand;
    private Properties props;
    private Timer t = null;
    
    private final Logger log = LoggerFactory.getLogger(EMWINSpout.class);

    private BlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>(100);

    public EMWINSpout(String name) {
        super(name);
    }

    private void startSpout() {

        con.connect();
        log.info("Starting producer()");
        this.packetProducer();
        
    }
    
    private void stopSpout() {
        log.error("Stopping Spout: Socket I/O exception raised.");
        con.close();
        con.getServerList().nextServer();
        
        log.error("Sleeping 3 seconds before restarting socket stream.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.warn("Thread sleep: " + e.getMessage());
        }
        this.startSpout();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void open(Map conf, TopologyContext context,
            SpoutOutputCollector collector) {

        _collector = collector;
        _rand = new Random();
        v = new EMWINValidator();
        sc = new EMWINScanner(v, con);

        super.open(conf, context, collector);

        this.startSpout();
    }

    private void packetProducer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sc.setIn();
                try {
                    while (sc.hasNext()) {
                        Packet p = sc.next();
                        queue.put(p);
                    }
                } catch (Exception e) {
                    log.warn("producer() stopped. " + e.getMessage());
                    stopSpout();
                }
            }
        }, "EMWIN Stream Producer Thread").start();

    }

    @Override
    public void nextTuple() {
        try {
            if (queue.size() < 1)
                return;
            else {
                long msgid = _rand.nextLong();
                Packet p = queue.take();
                _collector.emit(new Values(p, p.ft), msgid);
            }
        } catch (Exception e) {
            log.warn("nextTuple: " + e.getMessage());
            stopSpout();
        }
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("packet", "type"));
    }

    @Override
    public void onSignal(byte[] data) {
        String s = new String(data);
        log.info("Received signal: " + s);
        switch (s) {
            case "STOP":
                con.close();
                break;
        }
            
    }
}
