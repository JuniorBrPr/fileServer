package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class Session implements Runnable {
    private final Socket socket;
    private final FileSystem fileSystem;

    public Session(Socket socketForClient, FileSystem fileSystem) {
        this.socket = socketForClient;
        this.fileSystem = fileSystem;
        System.out.println("Client connected!");
    }

    @Override
    public void run() {
        String command = "";
        do {
            try (
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {
                System.out.println("here");
                int length = input.readInt();
                byte[] messageData = new byte[length];
                input.readFully(messageData, 0, messageData.length);

                command = new String(messageData).substring();
                System.out.println("Received command: " + command);
//                command = input.readUTF();
                String message;

//                if (command.equals("exit")) {
//                    message = "200";
//                    output.writeUTF(message);
//                    this.fileSystem.setExit(true);
//                    this.socket.close();
//                    break;
//                } else {
//                    try {
//                        message = switch (command.charAt(0)) {
//                            case '1' -> fileSystem.GET(command.substring(2));
//                            case '2' -> fileSystem.PUT(command.substring(2).split(" ")[0],
//                                    command.substring(2).split(" ")[1].getBytes());
//                            case '3' -> fileSystem.deleteFile(command.substring(2));
//                            default -> "501";
//                        };
//                    } catch (Exception e) {
//                        System.out.println("Error: " + e.getMessage());
//                        message = "501";
//                    }
//                }
//                output.writeUTF(message);
//                socket.close();
//                break;
            } catch (Exception e) {
                System.out.println("Error in session: " + e.getMessage() + " " + command);
                break;
            }
        } while (command.length() == 0 || !fileSystem.isExit());
    }
}
