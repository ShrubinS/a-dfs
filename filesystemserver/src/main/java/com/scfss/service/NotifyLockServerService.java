package com.scfss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotifyLockServerService {

    private final RestTemplate restTemplate;
    private final Environment environment;

    private String LOCKSERVER;

    @Autowired
    public NotifyLockServerService(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;

        this.LOCKSERVER = environment.getProperty("lockserver.address") + ":" + environment.getProperty("lockserver.port") + "/";
    }

    public void notifyFileCreated(String fileName) {
        String url = LOCKSERVER + "create-file";
        restTemplate.postForEntity(url, fileName, String.class);
    }

}
