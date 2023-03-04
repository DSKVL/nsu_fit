import ru.nsu.dskvl.bf.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
//
//class TestFactory {
//    @Test
//    public void testFactory() {
//        CommandFactory cf = new CommandFactory();
//        assertTrue(cf.getCommand('+') instanceof Addition);
//    }
//}

class TestInterpreter {
    @Test
    public void testInterpreter1() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        var bfi = new BrainfuckInterpreter(System.in, out);

        var programList  = Files.lines(Paths.get("src/test/resources/test1"))
                .map(String::chars)
                .flatMap(s -> s.mapToObj(n -> (char) n))
                .collect(Collectors.toList());
        bfi.start(programList);
        assertEquals("Hello World!\n", out.toString());
    }

    @Test
    public void testInterpreter2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        var bfi = new BrainfuckInterpreter(System.in, out);

        var programList  = Files.lines(Paths.get("src/test/resources/test2"))
                .map(String::chars)
                .flatMap(s -> s.mapToObj(n -> (char) n))
                .collect(Collectors.toList());
        bfi.start(programList);
        assertEquals("Hello World!\n", out.toString());
    }
}

class TestExecutionContext {
    @Test
    public void testExecutionContext() {

    }
}