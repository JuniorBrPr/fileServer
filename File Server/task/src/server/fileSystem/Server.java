package server.fileSystem;

import java.net.InetAddress;
import java.net.ServerSocket;

public class Server extends Thread {
    private final FileSystem fileSystem = new FileSystem();

    public void run() {
        System.out.println("Server started!");
        do {
            String ADDRESS = "127.0.0.1";
            int PORT = 23456;

            try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
                Session session = new Session(server.accept(), this.fileSystem);
                session.start();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } while (!this.fileSystem.isExit());
    }
}
