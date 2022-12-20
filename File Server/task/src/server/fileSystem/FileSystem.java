package server.fileSystem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;


public class FileSystem {
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;

    public FileSystem() {
        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            Session session = new Session(server.accept());
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

