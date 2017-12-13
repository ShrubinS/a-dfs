package com.ns.Controller;

import com.ns.dto.NameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
public class NameController {


    private final Environment environment;

    @Autowired
    public NameController(Environment environment) {
        this.environment = environment;
    }

    @RequestMapping(value = "/server-info", method = RequestMethod.GET)
    public NameResponse getFileServer(@RequestParam("filename") String filename) throws Exception {
        NameResponse ns = new NameResponse();
        String fileNameDecoded = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());
        ns.setFileSystemServerInfo(environment.getProperty(environment.getProperty("root")));
        ns.setFilePath(fileNameDecoded.replaceAll("/", "sep#"));
        return ns;
    }
}
