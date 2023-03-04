package ru.nsu.dskvl.bf;

import org.apache.logging.log4j.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interpreter for esoteric language Brainfuck.
 */
public class BrainfuckInterpreter {
    private final CommandFactory factory = new CommandFactory();
    private final InputStream inputStream;
    private final OutputStream outputStream;
    static final Logger log = LogManager.getRootLogger();

    /**
     * Takes input and output streams that brainfuck program will use.
     * @param inputStream
     * @param outputStream
     */
    public BrainfuckInterpreter(InputStream inputStream, OutputStream outputStream) {
        log.info("Creating interpreter");
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Interpereting function. Takes program as list of Characters.
     * @param input
     */
    public void start(List<Character> input) {
        log.info("Interpretation start");

        ExecutionContext context = new ExecutionContext(input, inputStream, outputStream);

        try {
            while (context.finished()) {
                Command command = factory.getCommand(input.get(context.getInstructionPointer()));
                command.execute(context);
            }
        } catch (BadCommandClassException | BadSyntaxException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}

