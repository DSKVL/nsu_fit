package ru.nsu.dskvl.bf;

import org.apache.logging.log4j.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Class that encapsulates all functionality of Brainfuck interpreter.
 */
public class ExecutionContext {
    private static final int defaultDataCapacity = 30000;

    private final byte[] data = new byte[defaultDataCapacity];
    private int cellPointer = 0;

    private int instructionPointer = 0;
    private final List<Character> instructionList;

    private boolean skipInstruction = false;
    private final Stack<Integer> loopLabels = new Stack<>();

    private final InputStream inputStream;
    private final OutputStream outputStream;

    Logger log = LogManager.getRootLogger();

    /**
     * Takes as parameter program in form of list of characters, output and
     * input stream for program.
     * @param instructionList a program.
     */
    ExecutionContext(List<Character> instructionList, InputStream inputStream, OutputStream outputStream) {
        log.info("Creating ExecutionContext");
        this.instructionList = instructionList;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Returns character from data at cellPointer.
     */
    public byte getData() {
        StringBuilder mes =new StringBuilder("ExecutionContext data access at ");
        log.info(mes.append(cellPointer).toString());
        return data[cellPointer];
    }

    /**
     * Sets character in data at cellPointer.
     */
    public void setData(byte setTo) {
        StringBuilder mes =new StringBuilder("ExecutionContext data modifying at ");
        if (skipInstruction) {
            log.info("ExecutionContext skip instuction");
            return;
        }
        log.info(mes.append(cellPointer));
        this.data[cellPointer] = setTo;
    }

    /**
     * returns cellPointer.
     */
    public int getCellPointer() {
        return cellPointer;
    }

    /**
     * Sets cellPointer.
     */
    public void setCellPointer(int cellPointer) {
        if (skipInstruction) {
            log.info("ExecutionContext skip instuction");
            return;
        }
        this.cellPointer = cellPointer;
    }

    /**
     * Returns Intruction pointer.
     */
    public int getInstructionPointer() {
        StringBuilder mes =new StringBuilder("ExecutionContext instruction pointer access. IP = ");
        log.info(mes.append(instructionPointer).toString());
        return instructionPointer;
    }

    /**
     * Sets instruction pointer.
     */
    public void setInstructionPointer(int instructionPointer) {
        StringBuilder mes =new StringBuilder("ExecutionContext instruction pointer modifying. IP = ");
        log.info(mes.append(instructionPointer).toString());
        this.instructionPointer = instructionPointer; }

    /**
     * Increments instruction pointer by 1.
     */
    public void incrementInstructionPointer() {
        log.info("IP incrementation");
        instructionPointer++;
    }

    /**
     * Puts address of the begining of the loop to the loopStack.
     */
    public void writeLoopLabel(int loopLabel) {
        StringBuilder mes =new StringBuilder("New loopLabel in stack. Value = ");
        log.info(mes.append(loopLabel).toString());
        loopLabels.push(loopLabel);
    }

    /**
     * Pops address of the begining of the loop from the loopStack.
     */
    public int popLoopLabel() {
        log.info("loopLabel pop");
        return loopLabels.pop(); }

    /**
     * Peeks address of the begining of the loop from the loopStack.
     */
    public int peekLoopLabel() {
        log.info("loopLabel peek");
        return  loopLabels.peek(); }

    /**
     * Checks SkipInstruction flag value.
     */
    public boolean getSkipInstruction() { return skipInstruction; }

    /**
     * Sets SkipInstruction flag value.
     */
    public void setSkipInstruction(boolean state) {
        StringBuilder mes =new StringBuilder("New setInstruction. Value = ");
        log.info(mes.append(state).toString());
        skipInstruction = state;
    }

    /**
     * Checks if program has reached its end.
     */
    public boolean finished() {
        return instructionPointer < instructionList.size();
    }

    /**
     * Returns imputStream that was specified in constructor.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Prints character from data at cellPointer.
     */
    public void printCurrentData() {
        if (skipInstruction) {
            log.info("ExecutionContext skip instuction");
            return;
        }

        try {
            byte c = data[cellPointer];
            StringBuilder mes =new StringBuilder("Printing char: ");
            log.info(mes.append(c).toString());
            outputStream.write(c);
        } catch (IOException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }
}
