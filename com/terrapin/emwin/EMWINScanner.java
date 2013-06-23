package com.terrapin.emwin;

public class EMWINScanner {

	private EMWINInputStream i;
	private EMWINValidator v;
	private StringBuffer header;
	private byte[] body;
	private EMWINPacket p;

	public EMWINScanner(EMWINInputStream in, EMWINValidator vin) {
		i = in;
		v = vin;
	}

	public boolean hasNext() throws java.io.IOException {
		scan();
		p = new EMWINPacket(v);
		try {
			p.setHeader(header.toString());
			p.setBody(body);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public EMWINPacket next() {
		return p;
	}

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
