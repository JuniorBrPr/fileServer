package server.fileSystem;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;
    private final FileSystem fileSystem = new FileSystem();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Server() {
        try (ServerSocket server = new ServerSocket(this.PORT, 50, InetAddress.getByName(this.ADDRESS))) {
            System.out.println("Server started!");
            do {
                executor.submit(new Session(server.accept(), fileSystem));
            } while (!this.fileSystem.isExit());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
