package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(int port) {
        client = new ChessClient(port);
    }


    public void run() {
        System.out.println("Welcome to chess! Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while(true) {
            String status = "(" + client.getState() + ")";
            System.out.print("\n" + status + " >>> ");
            printPrompt();
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            try {
                String response = client.evaluateCommand(input);
                System.out.print(response);
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printPrompt(){
        System.out.print(" ");
    }
}
