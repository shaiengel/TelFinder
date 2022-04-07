package com.shai.app;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationReader {

    Configuration config;

    public ConfigurationReader(){
        int offset;
        try
        {
            config = new PropertiesConfiguration("./config/application.properties");
        }
        catch (ConfigurationException cex)
        {
            System.err.println("ConfigurationReader ConfigurationException");
        }
    }

    public String getCsvFile() {
        return config.getString("csv.filename");
    }
    public Integer getPoolSize() {
        return config.getInt("tel.thread.poolSize");
    }




}