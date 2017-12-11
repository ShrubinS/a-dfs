package com.scfss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotifyLockServerService {

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Autowired
    public NotifyLockServerService(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    public void notifyFileCreated(String fileName) {
        String lockserverInfo = environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port");
        restTemplate.put(lockserverInfo, fileName);
    }

}
