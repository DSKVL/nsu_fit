package ru.nsu.dskvl.bf;

public class LoopStart implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        executionContext.writeLoopLabel(executionContext.getInstructionPointer());
        if (0 == executionContext.getData()) {
            executionContext.setSkipInstruction(true);
        }
    }
}
