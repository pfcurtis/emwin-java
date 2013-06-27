

package com.terrapin.emwin;

import java.io.*;

/**
 * This class encapsulates the EMWIN data stream. For reasons unknown, the data in the stream is XOR'd with 255 to provide some sort of "encrytption".
 * This was probably done by the original data stream software vendor to obscure the data.
 * <p>
 * The class does not extend the DataInputStream, but contains most of the methods in that class.
 * 
 * @author pcurtis
 *
 */
public class EMWINInputStream {
    private InputStream i;
    private DataInputStream dis;

    /**
     * Create a EMWIN data stream for reading. The user must create and connect to an EMWIN data source.
     * @param in the connected input stream
     */
    public EMWINInputStream(InputStream in) {
        i = in;
        dis = new DataInputStream(in);
    }

    /**
     * Reads a single decoded byte from the data stream
     * @return single decoded byte
     * @throws java.io.IOException
     */
    public int readUnsignedByte() throws java.io.IOException {
        return (dis.readUnsignedByte() ^ 255);
    }

    /**
     * Read and decode the data stream, filling a byte array
     * @param b byte array to be filled
     * @throws java.io.IOException
     */
    public void readFully(byte b[]) throws java.io.IOException {
        dis.readFully(b);
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(b[i] ^ 255);
    }

    /**
     * Reads and decodes bytes from the data stream 
     * @param b the byte array into which the data is read.
     * @param off an int specifying the offset into the data.
     * @param len an int specifying the number of bytes to read.
     * @throws java.io.IOException
     */
    public void readFully(byte[] b, int off, int len) throws java.io.IOException {
        dis.readFully(b, off, len);
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)(b[i] ^ 255);
    }

    /**
     * Returns the underlying DataInputStream
     * @return the underlying DataInputStream
     */
    public DataInputStream getDataInputStream() {
        return dis;
    }

    /**
     * Closes the underlying input streams
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException {
        dis.close();
        i.close();
    }
}
