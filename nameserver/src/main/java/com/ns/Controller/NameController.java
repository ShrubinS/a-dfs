package com.ns.Controller;

import com.ns.dto.NameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NameController {


    private final Environment environment;

    @Autowired
    public NameController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/{filepath}")
    public NameResponse getFileServer(@PathVariable("filepath") String filepath) {
        NameResponse ns = new NameResponse();
        ns.setFileSystemServerInfo(environment.getProperty(environment.getProperty("root")));
        return ns;
    }
}
