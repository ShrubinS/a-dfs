package com.client.cmdline;


import com.client.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

@Component
public class CommandLiner implements CommandLineRunner {

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

        System.out.println("Enter operation. List of available operations are... ");
        /*
            TODO: add file writing
         */

        String workingDir = environment.getProperty("client.wdir");

        try(Scanner in = new Scanner(System.in)) {

            String command;

            while (!(command = in.nextLine()).equals("exit")) {
                String[] commands =  command.split(" ");
                String op = commands[0];
                String fileName = commands[1];

                switch (op) {
                    case "r":
                        fileService.readFile(fileName);
                        break;
                    case "w":
                        String write = commands[2];
                        fileService.writeFile(fileName, write);
                        break;
                    case "a":
                        String append = commands[2];
                        fileService.appendToFile(fileName, append);
                        break;
                    case "ls":
                        fileService.listFiles(workingDir);
                        break;
                    case "cd":
                        workingDir = fileService.changeDir(fileName);
                        break;
                }
            }
        }

    }

}