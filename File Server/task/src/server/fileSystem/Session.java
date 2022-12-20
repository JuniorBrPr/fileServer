package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Session extends Thread {
    private final Socket socket;
    private final String MESSAGE = "All files were sent!";

    public Session(Socket socketForClient) {
        this.socket = socketForClient;
    }

    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = input.readUTF();
            System.out.println("Received: " + msg);

            output.writeUTF(MESSAGE);
            System.out.println("Sent: " + MESSAGE);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
