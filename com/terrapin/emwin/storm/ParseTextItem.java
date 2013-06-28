package com.terrapin.emwin.storm;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terrapin.emwin.object.TextItem;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class ParseTextItem extends BaseBasicBolt implements IBasicBolt {

	public final Logger log = LoggerFactory.getLogger(ParseTextItem.class);
	private static final String ISSUER_REGEX = "(\\w{4}\\w*)\\s+([B|R|N|T|P|K|C]\\w{3})\\s+(\\d{6})";
	private static final String DATE_REGEX = "(\\d{1,2})(\\d{2}) ([A|P]M) ([A-Z][A-Z][A-Z]) [A-Z][A-Z][A-Z] ([A-Z][A-Z][A-Z]) ([0-9]+) ([0-9]+)";
	private static final String NEW_STATE = "([A-Z][A-Z])([C|Z])(\\d{3}|ALL)-(.*)";

	private Pattern issuer;
	private Pattern nwsDate;
	private Pattern newState;

	private Matcher m;
	private TextItem t;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		issuer = Pattern.compile(ISSUER_REGEX);
		nwsDate = Pattern.compile(DATE_REGEX);
		newState = Pattern.compile(NEW_STATE);
		log.info("compiled regexes");
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declareStream("text_item", new Fields("item"));
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		t = (TextItem) input.getValueByField("item");
		log.info(t.getPacketFileName()+"."+t.getPacketFileType()+" "+t.getPacketDate().getTime());

		Scanner scanner = new Scanner(t.getBody());
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			// process the line
			m = issuer.matcher(line);
			if (m.matches()) {
				log.debug("mtype=" + m.group(1) + "  wid=" + m.group(2) + "  date=" + m.group(3));
			}
			
			m = nwsDate.matcher(line);
			if (m.matches()) {
				log.debug(m.group(1) + m.group(2) + " " + m.group(3));
			}
			
			m = newState.matcher(line);
			if (m.matches()) {
				log.info("st="+m.group(1) + "  z="+m.group(2) + "  c="+m.group(3));
				String[] sscan = m.group(4).split("-");
				for (int x = 0; x < sscan.length; x++)
					log.info("  c="+sscan[x]);
				
			}
			
		}
	}

}
