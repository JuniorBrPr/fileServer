package server.fileSystem;

import java.net.InetAddress;
import java.net.ServerSocket;

class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

public class Server extends Thread {
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;
    private final String FILESYSTEM_PATH = "server/data";
    private FileSystem fileSystem = new FileSystem();

    public void run() {
        System.out.println("Server started!");
        do {
            try (ServerSocket server = new ServerSocket(this.PORT, 50, InetAddress.getByName(this.ADDRESS))) {
                Session session = new Session(server.accept(), this.fileSystem);
                session.start();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } while (!this.fileSystem.isExit());
    }
}
