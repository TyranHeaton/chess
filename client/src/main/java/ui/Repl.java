package ui;

import java.util.Scanner;

public class Repl {

    public void run() {
        System.out.println("Welcome to chess! Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while(true) {
            printPrompt();
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            try {
                // TODO: Call the client
                System.out.println("You entered: " + input);
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printPrompt(){
        System.out.print("> ");
    }
}
