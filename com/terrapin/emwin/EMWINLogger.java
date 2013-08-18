/**
 * 
 */
package com.terrapin.emwin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.mapr.franz.catcher.Client;
import com.terrapin.emwin.object.Packet;

/**
 * @author pcurtis
 * 
 */
public class EMWINLogger {

    private EMWINScanner sc;
    private EMWINValidator v;
    private EMWINConnection con = new EMWINConnection();
    private Client fc;
    private String franzHost = null;
    private int franzPort = 0;
    private String franzTopic = null;
    private Properties props;
    
    private final Logger log = LoggerFactory.getLogger(EMWINLogger.class);

    /**
     * @param args
     * @throws ServiceException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ServiceException {
        EMWINLogger em = new EMWINLogger();
        em.startStream();
    }

    private void startStream() throws IOException, ServiceException {
        props = EMWINProperties.loadProperties();
        franzTopic = props.getProperty("franz.topic", "emwin");
        franzHost = props.getProperty("franz.host", "localhost");
        franzPort = new Integer(props.getProperty("franz.port", "8088")).intValue();
        fc = new Client(Lists.newArrayList(new PeerInfo(franzHost, franzPort)));
        con.connect();
        v = new EMWINValidator();
        sc = new EMWINScanner(v, con);
        producer();
    }

    private void producer() {
        do {
            try {
                while (sc.hasNext()) {
                    Packet p = sc.next();
                    fc.sendMessage(franzTopic,
                            ByteBuffer.wrap(SerializationUtils.serialize(p)));
                    log.debug("Sent '" + p.fn + "'");
                }
            } catch (Exception e) {
                log.warn("producer() stopped", e);
            }
        } while (true);
    }

}
