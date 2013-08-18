package com.terrapin.emwin;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class EMWINProperties {
    
    public static final Logger log = LoggerFactory.getLogger(EMWINProperties.class);

    public static Properties loadProperties() {
        Properties props = new Properties();
        loadProperties("emwin-java.properties", props);
        return props;
    }

    private static Properties loadProperties(String resource, Properties props) {
        try {
            InputStream is = Resources.getResource(resource).openStream();
            log.info("Loading properties from '" + resource + "'.");
            props.load(is);
        } catch (Exception e) {
            log.info("Not loading properties from '" + resource + "'.");
            log.info(e.getMessage());
        }
        return props;
    }

}
