package ru.nsu.dskvl.bf;

public class LoopEnd implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        if (executionContext.getSkipInstruction()) {
            executionContext.setSkipInstruction(false);
            executionContext.popLoopLabel();
        } else {
            if (executionContext.getData() != 0 ) {
                executionContext.setInstructionPointer(executionContext.peekLoopLabel());
            } else {
                executionContext.popLoopLabel();
            }
        }
    }
}
