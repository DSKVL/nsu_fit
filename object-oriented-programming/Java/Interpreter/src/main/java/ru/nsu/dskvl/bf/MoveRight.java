package ru.nsu.dskvl.bf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveRight implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        if (executionContext.getCellPointer() < 29999 ) {
            executionContext.setCellPointer(executionContext.getCellPointer() + 1);
            return;
        }
        Logger log = LogManager.getRootLogger();
        log.error("Out of data boundaries");
        System.err.println("Out of data boundaries");
    }
}
