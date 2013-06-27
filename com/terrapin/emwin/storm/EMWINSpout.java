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

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.terrapin.emwin.EMWINHeartbeat;
import com.terrapin.emwin.EMWINInputStream;
import com.terrapin.emwin.EMWINPacket;
import com.terrapin.emwin.EMWINScanner;
import com.terrapin.emwin.EMWINValidator;

public class EMWINSpout extends BaseRichSpout {

	private SpoutOutputCollector _collector;
	private EMWINScanner sc;
	private EMWINValidator v;
	private Socket emwinSocket = null;
	private OutputStream out = null;
	private EMWINInputStream in = null;
	private Random _rand;

	private final Logger log = LoggerFactory.getLogger(EMWINSpout.class);

	private BlockingQueue<EMWINPacket> queue = new ArrayBlockingQueue<EMWINPacket>(
			100);

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		_collector = collector;
		_rand = new Random();
		Properties props = EMWINTopology.loadProperties();

		String host = props.getProperty("emwin.host");
		int port = Integer.parseInt(props.getProperty("emwin.port"));

		try {
			log.info("connecting");
			emwinSocket = new Socket(host, port);
			out = emwinSocket.getOutputStream();
			in = new EMWINInputStream(emwinSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host '" + host + "'");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection");
			System.exit(1);
		}

		Timer t = new Timer("Heartbeat", true);
		t.schedule(new EMWINHeartbeat(out), 0, 300000);

		v = new EMWINValidator();
		sc = new EMWINScanner(in, v);
		log.info("Starting producer()");
		producer();
	}

	private void producer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (sc.hasNext()) {
						EMWINPacket p = sc.next();
						queue.put(p);
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
				long msgid = _rand.nextLong();
				EMWINPacket p = queue.take();
				_collector.emit(new Values(p, p.ft), msgid);
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
		declarer.declare(new Fields("packet", "type"));
	}
}
