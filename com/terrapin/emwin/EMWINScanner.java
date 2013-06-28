package com.terrapin.emwin;

import com.terrapin.emwin.object.Packet;

public class EMWINScanner {

	private EMWINInputStream i;
	private EMWINValidator v;
	private StringBuffer header;
	private byte[] body;
	private Packet p;

	/**
	 * This class examines the incoming byte stream for specific character sequences. The sequences signal the start of the two types
	 * of packets received: data packets and server lists. Data packets always begin with "/PF" while server list packets begin with "/ServerList/"
	 * <p>
	 * Once the data packet start is detected, an EMWINPacket is constructed and validated
	 * 
	 * @param in the raw data stream
	 * @param vin an instance of the validator class
	 */
	public EMWINScanner(EMWINInputStream in, EMWINValidator vin) {
		i = in;
		v = vin;
	}

	/**
	 * This method blocks until a complete packet has been received. When a full packet is available, the method returns. This method
	 * would be used as the test in a while() loop.
	 * 
	 * @return true when a packet is available
	 * @throws java.io.IOException
	 */
	// This method blocks until a packet is available
	public boolean hasNext() throws java.io.IOException {
		scan();
		p = new Packet(v);
		try {
			p.setHeader(header.toString());
			p.setBody(body);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This method returns the constructed EMWINPacket. This method would be used inside a while() to retrieve the data.
	 *   
	 * @return complete data packet
	 * @see Packet
	 */
	public Packet next() {
		return p;
	}

	/* We are scanning for two types of packets. A data packet starts with "/PF" and the server list starts with "/ServerList" */
	private void scan() throws java.io.IOException {

		int l;
		int b;
		byte[] hdr = new byte[75];
		char[] hdr_c = new char[75];

		while (true) {
			b = i.readUnsignedByte();

			if (b == '/') {
				b = i.readUnsignedByte();
			} else {
				// System.out.println("read1: "+b+" '"+(char)b+"'");
				continue;
			}

			if (b == 'P') {
				b = i.readUnsignedByte();
			} else {
				continue;
			}

			if (b == 'F') {
				header = new StringBuffer("/PF");
				i.readFully(hdr);
				for (int i = 0; i < hdr.length; i++)
					hdr_c[i] = (char) hdr[i];
				header.append(hdr_c);
				// Remove the CR + NL characters
				i.getDataInputStream().read();
				i.getDataInputStream().read();
				body = new byte[1024];
				i.readFully(body);
				break;
			} else {
				continue;
			}
		}
	}
}
