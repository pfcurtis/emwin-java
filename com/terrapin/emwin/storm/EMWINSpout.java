

package com.terrapin.emwin.storm;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import com.terrapin.emwin.*;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class EMWINSpout extends BaseRichSpout {

    SpoutOutputCollector _collector;
    Random _rand;
    EMWINScanner sc;
    EMWINValidator v;
    Socket echoSocket = null;
    OutputStream out = null;
    EMWINInputStream in = null;

    private BlockingQueue<EMWINPacket> queue = new ArrayBlockingQueue<EMWINPacket>(100);

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

        _collector = collector;
        _rand = new Random();


        try {
            echoSocket = new Socket("www.opennoaaport.net", 2211);
            out = echoSocket.getOutputStream();
            in = new EMWINInputStream(echoSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection");
            System.exit(1);
        }

        Timer t = new Timer("Heartbeat", true);
        t.schedule(new EMWINHeartbeat(out), 0, 300000);

        sc = new EMWINScanner(in);
        v = new EMWINValidator();
        producer();
    }

    private void producer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (sc.hasNext()) {
                        EMWINPacket p = sc.next();
                        if (v.checkHeader(p))
                            queue.put(p);
                        else
                            System.out.println("Bad packet -");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                EMWINPacket p = (EMWINPacket)queue.take();
                _collector.emit(new Values(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        declarer.declare(new Fields("packet"));
    }
}

