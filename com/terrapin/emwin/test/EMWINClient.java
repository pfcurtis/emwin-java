package com.terrapin.emwin.test;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.ParseException;
import java.util.Properties;
import com.google.common.io.Resources;

import com.terrapin.emwin.*;
import com.terrapin.emwin.object.Packet;
import com.terrapin.emwin.object.PacketException;

public class EMWINClient {

	public static Properties loadProperties() {
		Properties props = new Properties();
		loadProperties("emwin-java.properties", props);
		return props;
	}

	private static Properties loadProperties(String resource, Properties props) {
		try {
			InputStream is = Resources.getResource(resource).openStream();
			System.err.println("Loading properties from '" + resource + "'.");
			props.load(is);
		} catch (Exception e) {
			System.err.println("Not loading properties from '" + resource
					+ "'.");
			System.err.println(e.getMessage());
		}
		return props;
	}

	public static void main(String[] args) throws IOException,
			PacketException, ParseException {

		Socket emwinSocket = null;
		OutputStream out = null;
		EMWINInputStream in = null;

		Properties props = loadProperties();

		String host = props.getProperty("emwin.host");
		int port = Integer.parseInt(props.getProperty("emwin.port"));

		try {
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

		EMWINValidator v = new EMWINValidator();
		EMWINScanner sc = new EMWINScanner(in, v);

		while (sc.hasNext()) {
			Packet p = sc.next();
			if (v.checkHeader(p))
				System.out.println(p.pn + " of " + p.pt + ": " + p.fn + " "
						+ p.fd + " +");
			else
				System.out.println(p.pn + " of " + p.pt + ": " + p.fn + " "
						+ p.fd + " -");
		}

		out.close();
		in.close();
		emwinSocket.close();
	}
}
