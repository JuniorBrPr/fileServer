package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import static server.fileSystem.SerializingUtil.serialize;

class Session implements Runnable {
    private static final Logger logger = Logger.getLogger(Session.class.getName());
    private static final String EXIT_COMMAND = "exit";
    private static final String GET_COMMAND = "1";
    private static final String PUT_COMMAND = "2";
    private static final String DELETE_COMMAND = "3";

    private final Socket socket;
    private final FileSystem fileSystem;
    private final ServerSocket serverSocket;

    Session(Socket socketForClient, FileSystem fileSystem, ServerSocket serverSocket) {
        this.socket = socketForClient;
        this.fileSystem = fileSystem;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String command = input.readUTF();
            switch (command) {
                case EXIT_COMMAND:
                    this.fileSystem.setExit(true);
                    serialize(this.fileSystem, Server.SERVER_DATA_ROOT);
                    socket.close();
                    serverSocket.close();
                    break;
                case GET_COMMAND:
                    fileSystem.GET(input, output);
                    run();
                    break;
                case PUT_COMMAND:
                    fileSystem.PUT(input, output);
                    run();
                    break;
                case DELETE_COMMAND:
                    fileSystem.DELETE(input, output);
                    break;
                default:
                    System.out.println("Unrecognized command: " + command);
            }
        } catch (Exception e) {
            System.out.println("Error while processing command: " + e.getMessage());
        }
    }
}
