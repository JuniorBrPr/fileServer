package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static server.fileSystem.SerializingUtil.serialize;

class Session implements Runnable {
    private final Socket socket;
    private final FileSystem fileSystem;
    private final ServerSocket serverSocket;

    public Session(Socket socketForClient, FileSystem fileSystem, ServerSocket serverSocket) {
        this.socket = socketForClient;
        this.fileSystem = fileSystem;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        String command = "";
        do {
            try (
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {
                command = input.readUTF();

                if (command.equals("exit")) {
                    this.fileSystem.setExit(true);
                    serialize(this.fileSystem, Server.SERVER_DATA_ROOT);
                    socket.close();
                    serverSocket.close();
                    break;
                } else {
                    try {
                        switch (command.charAt(0)) {
                            case '1' -> {
                                fileSystem.GET(input, output);
                                socket.close();
                            }
                            case '2' -> {
                                fileSystem.PUT(input, output);
                                socket.close();

                            }
                            case '3' -> {
                                fileSystem.DELETE(input, output);
                                socket.close();
                            }
                            default -> System.out.println(501);
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (command.length() == 0 || !fileSystem.isExit());
    }
}
