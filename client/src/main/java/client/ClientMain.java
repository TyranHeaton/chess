package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        new Repl(port).run();
    }
}
