package ru.nsu.dskvl.bf;

public class Print implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        executionContext.printCurrentData();
    }
}
