package util;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class ConfigurationHandler {
    private static final String CONFIG_FILE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.properties";;
    private static Properties properties;
    private static volatile ConfigurationHandler instance;
    
    private ConfigurationHandler() {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE);
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Singleton pattern
    public static ConfigurationHandler getInstance() {
       if (instance == null) {
            synchronized (ConfigurationHandler.class) {
                if (instance == null) {
                    instance = new ConfigurationHandler();
                }
            }
        }
        return instance;
    }
   
    public String getRobotHost() {
        return properties.getProperty("robotHost");
    }

    public String getEndpointAdministratorServer() {
        return properties.getProperty("endpointAdministratorServer");
    }
    
    public String getEndpointBroker() {
        return properties.getProperty("endpointBroker");
    }

    public String getMqttTopic() {
        return properties.getProperty("mqttTopic");
    }

    public String getSlidingWindowSize() {
        return properties.getProperty("slidingWindowSize");
    }
    
    public String getSlidingWindowOverlap() {
        return properties.getProperty("slidingWindowOverlap");
    }
}
