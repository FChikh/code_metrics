package org.metrics.example;

/**
 * A sample class containing methods of varying complexity.
 */
public class ExampleComplexity {

    public void simpleMethod() {
        System.out.println("This is a simple method with 0 branches.");
    }

    public void conditionalMethod(int value) {
        if (value > 10) {
            System.out.println(">10");
        } else {
            System.out.println("<=10");
        }
    }

    public int multipleReturns(int value) {
        switch (value) {
            case 0:
                return value;
            case 1:
                return value - (value * 2);
            case 2:
                return value - (value * 3);
            default:
                return value - 3;
        }
    }

    public void loopMethod(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println("Iteration: " + i);
        }
    }

    public void switchMethod(int choice) {
        switch (choice) {
            case 1:
                System.out.println("It's number 1.");
                break;
            case 42:
                System.out.println("You've found the answer.");
                break;
            default:
                System.out.println("Another nice number.");
                break;
        }
    }

    public void complexMethod() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("Outer loop iteration: " + i);
                for (int j = 0; j < 3; j++) {
                    System.out.println("Inner loop iteration: " + j);
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            System.out.println("Execution completed.");
        }
    }

    public void is_snake_case() {}
    public void IsPascalCase() {}
    public void IS_COBOL_CASE() {}
}
