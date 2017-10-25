package com.tairanchina.csp.dew.core;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.tairanchina.csp.dew.core.hystrix.FailureEventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class CloudAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CloudAutoConfiguration.class);

    @Autowired
    private FailureEventNotifier failureEventNotifier;

    @PostConstruct
    public void init() throws IOException {
        logger.info("Enabled Failure Event Notifier");
        HystrixPlugins.getInstance().registerEventNotifier(failureEventNotifier);
    }


}