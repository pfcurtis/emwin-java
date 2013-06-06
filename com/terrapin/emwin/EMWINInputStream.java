

package com.terrapin.emwin;

import java.io.*;

public class EMWINInputStream {
    private InputStream i;
    private DataInputStream dis;

    public EMWINInputStream(InputStream in) {
        i = in;
        dis = new DataInputStream(in);
    }

    public int readUnsignedByte() throws java.io.IOException {
        return (dis.readUnsignedByte() ^ 255);
    }

    public void readFully(byte b[]) throws java.io.IOException {
        dis.readFully(b);
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(b[i] ^ 255);
    }

    public void readFully(byte[] b, int off, int len) throws java.io.IOException {
        dis.readFully(b, off, len);
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(b[i] ^ 255);
    }

    public DataInputStream getDataInputStream() {
        return dis;
    }

    public void close() throws java.io.IOException {
        dis.close();
        i.close();
    }
}
