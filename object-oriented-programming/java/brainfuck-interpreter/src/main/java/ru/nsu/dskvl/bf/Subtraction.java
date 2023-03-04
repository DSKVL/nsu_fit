package ru.nsu.dskvl.bf;

public class Subtraction implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        byte currentData = executionContext.getData();
        executionContext.setData((byte)(currentData - 1));
    }
}
