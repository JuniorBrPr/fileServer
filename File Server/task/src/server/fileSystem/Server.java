package server.fileSystem;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static server.fileSystem.SerializingUtil.deserialize;
import static server.fileSystem.SerializingUtil.serialize;


public class Server {
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;
    private final String USER_FILES_ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\" +
            "File Server\\task\\src\\server\\data";
    static final String SERVER_DATA_ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\" +
            "File Server\\task\\src\\server\\fileSystem\\data\\data.ser";
    private FileSystem fileSystem;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Server() {
        try {
            this.fileSystem = deserialize(SERVER_DATA_ROOT) == null ? new FileSystem(USER_FILES_ROOT) :
                    deserialize(SERVER_DATA_ROOT);
        } catch (Exception e) {
            System.out.println("Error while deserializing: " + e.getMessage());
            e.printStackTrace();
            this.fileSystem = new FileSystem(USER_FILES_ROOT);
            this.fileSystem.loadFiles();
        }


        try (ServerSocket server = new ServerSocket(this.PORT, 50, InetAddress.getByName(this.ADDRESS))) {
            System.out.println("Server started!");
            while (!this.fileSystem.isExit()) {
                this.executor.submit(new Session(server.accept(), this.fileSystem, server));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Server stopped!");

        try {
            serialize(this.fileSystem, SERVER_DATA_ROOT);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error while serializing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class Main {
    public static void main(String[] args) {
        Server server = new Server();
    }
}
