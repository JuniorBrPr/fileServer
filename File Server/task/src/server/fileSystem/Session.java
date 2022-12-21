package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class Session extends Thread {
    private final Socket socket;
    private FileSystem fileSystem;

    public Session(Socket socketForClient, FileSystem fileSystem) {
        this.socket = socketForClient;
        this.fileSystem = fileSystem;
    }

    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String command = input.readUTF();
            String message;

            if (command.equals("exit")) {
                message = "200";
                fileSystem.setExit(true);
            } else {
                try {
                    message = switch (command.charAt(0)) {
                        case '1' -> fileSystem.getFile(command.substring(2));
                        case '2' -> fileSystem.addFile(command.substring(2).split(" ")[0],
                                command.substring(2).split(" ")[1].getBytes());
                        case '3' -> fileSystem.deleteFile(command.substring(2));
                        default -> "501";
                    };
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    message = "501";
                }
            }
            output.writeUTF(message);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
