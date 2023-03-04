package ru.nsu.dskvl.bf;

import java.util.Scanner;

public class Read implements Command{
    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.incrementInstructionPointer();
        try (Scanner sc = new Scanner(executionContext.getInputStream())){
            executionContext.setData((byte) sc.next().charAt(0));
        }
    }
}
