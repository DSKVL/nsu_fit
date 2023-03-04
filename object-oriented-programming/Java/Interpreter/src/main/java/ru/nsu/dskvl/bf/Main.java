package ru.nsu.dskvl.bf;

import org.apache.logging.log4j.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;


public class Main {
    private static final String programFile = "src/main/resources/in";
    static final Logger log = LogManager.getRootLogger();
    public static void main(String[] args) {
        log.info("Application start");
        try {
            StringBuilder mes = new StringBuilder("Trying to open programFile at");
            log.info(mes.append(programFile).toString());

            var programList  = Files.lines(Paths.get(programFile))
                                                 .map(String::chars)
                                                 .flatMap(s -> s.mapToObj(n -> (char) n))
                                                 .collect(Collectors.toList());

            BrainfuckInterpreter bfInterpreter = new BrainfuckInterpreter(System.in, System.out);
            bfInterpreter.start(programList);

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
