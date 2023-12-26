package org.elyte.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private static final String CONFIG_FILE = "src/config.yml";

    private Properties properties;

    public AppConfig() {

        try (FileInputStream configInput = new FileInputStream(CONFIG_FILE)) {
            this.properties = new Properties();
            this.properties.load(configInput);
        } catch (FileNotFoundException e) {
            log.error("[+] Config Exception ", e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("[+] Config Exception ", e.getLocalizedMessage());
        }

    }

    public String getConfigValue(String property) {
        String value = this.properties.getProperty(property);
        if (value == null){
            throw new RuntimeException("Config Error. Invalid property key!");
        }
        return value;
    }

}
