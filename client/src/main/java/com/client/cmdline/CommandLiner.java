package com.client.cmdline;


import com.client.service.FileService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Component
public class CommandLiner implements CommandLineRunner {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CommandLiner.class);

    private final Environment environment;
    private final FileService fileService;
    private final File opsFile;

    @Autowired
    public CommandLiner(Environment environment, FileService fileService, File opsFile) {
        this.environment = environment;
        this.fileService = fileService;
        this.opsFile = opsFile;
    }

    /*
        args[0] : file name
        args[1] : file operations

        Available operations:
            r   : read file. If file does not exist, throw error (no lock required)
            w   : overwrite file if exists, or create new file
            a   : append to existing file, if file does not exist, throw error
            ls  : show files currently in directory (no lock required)
            cd  : move into directory
            help: show list of available ops
            exit/quit: quit
     */
    @Override
    public void run(String... args) throws Exception {
        if (args == null) {
            throw new UnsupportedOperationException("file argument or operation argument is missing");
        }

        System.out.println("Enter operation. For list of available operations type --help");
        /*
            TODO: add file writing
         */

        String workingDir = environment.getProperty("client.wdir");

        try(Scanner in = new Scanner(System.in)) {

            String command;

            System.out.print("\n@" + workingDir + " ");
            while (!(command = in.nextLine()).equals("exit")) {
                String[] commands =  command.trim().split(" ");
                String op = "";
                String fileName = null;
                String inputText = "";
                try {
                    op = commands[0];
                    fileName = commands[1];
                    inputText = command.substring(command.indexOf("\"") + 1, command.lastIndexOf("\""));
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
                String filePath;
                if (fileName != null && !fileName.startsWith("/")) {
                    filePath = workingDir + fileName;
                } else {
                    filePath = fileName;
                }

                try {
                    switch (op) {
                        case "r":
                            fileService.readFile(filePath);
                            break;
                        case "w":
                            String write = inputText;
                            fileService.writeFile(filePath, write, "w");
                            break;
                        case "a":
                            String append = inputText;
                            fileService.appendToFile(filePath, append);
                            break;
                        case "ls":
                            fileService.listFiles(workingDir);
                            break;
                        case "cd":
                            workingDir = fileService.changeDir(workingDir, fileName);
                            break;
                        case "--help":
                            byte[] text = Files.readAllBytes(Paths.get(opsFile.getAbsolutePath()));
                            System.out.write(text);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                System.out.print("\n@" + workingDir + " ");
            }
        }

    }

}