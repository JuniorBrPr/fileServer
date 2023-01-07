package server.fileSystem;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 23456;
    private static final String USER_FILES_ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\" +
            "File Server\\task\\src\\server\\data";
    static final String SERVER_DATA_ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\" +
            "File Server\\task\\src\\server\\fileSystem\\data\\data.ser";
    private FileSystem fileSystem;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Server() {
        try {
            this.fileSystem = SerializingUtil.deserialize(SERVER_DATA_ROOT) == null ? new FileSystem(USER_FILES_ROOT) :
                    SerializingUtil.deserialize(SERVER_DATA_ROOT);
        } catch (Exception e) {
            System.out.println("Error while deserializing: " + e.getMessage());
            this.fileSystem = new FileSystem(USER_FILES_ROOT);
            this.fileSystem.loadFiles();
        }

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (!this.fileSystem.isExit()) {
                System.out.println("Waiting for a connection...");
                this.executor.submit(new Session(server.accept(), this.fileSystem, server));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Server stopped!");

        try {
            SerializingUtil.serialize(this.fileSystem, SERVER_DATA_ROOT);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error while serializing: " + e.getMessage());
        }
    }
}