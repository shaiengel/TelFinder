package com.shai.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages={"com.shai"})
public class Startup {

    @Autowired
    ConfigurationReader config;

    public static void main(String[] args) {
        SpringApplication.run(Startup.class, args);
    }

    @PostConstruct
    public void start() throws InterruptedException {
        String file = config.getCsvFile();
        ThreadTelephoneName thrdTelephoneName = new ThreadTelephoneName(file);
        thrdTelephoneName.start();

        ThreadBufferSize thrdBufferSize = new ThreadBufferSize(file);
        thrdBufferSize.start();
    }
}

