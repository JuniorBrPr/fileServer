package client.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class FileSystemClient extends Thread {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        String ADDRESS = "127.0.0.1";
        int PORT = 23456;

        try (
                Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            boolean correctCommand = false;
            do {
                System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
                String command = scanner.nextLine();

                if (command.matches("exit")) {
                    output.writeUTF(command);
                    System.out.println("The request was sent.");
                    System.exit(1);
                } else {
                    switch (command.charAt(0)) {
                        case '1' -> {
                            getFile(command, input, output);
                            correctCommand = true;
                        }
                        case '2' -> {
                            addFile(command, input, output);
                            correctCommand = true;
                        }
                        case '3' -> {
                            deleteFile(command, input, output);
                            correctCommand = true;
                        }
                        default -> System.out.println("Incorrect command!");
                    }
                }
            } while (!correctCommand);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void menu() {
//        System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
//        boolean correctCommand = false;
//        do {
//            String command = scanner.nextLine();
//
//            if (command.matches("exit")) {
//                output.writeUTF(command);
//                System.out.println("The request was sent.");
//                System.exit(1);
//            } else {
//                switch (command.charAt(0)) {
//                    case '1':
//                        getFile(command, input, output);
//                        correctCommand = true;
//                        break;
//                    case '2':
//                        addFile(command, input, output);
//                        correctCommand = true;
//                        break;
//                    case '3':
//                        deleteFile(command, input, output);
//                        correctCommand = true;
//                        break;
//                    default:
//                        System.out.println("Incorrect command!");
//                }
//            }
//        } while (!correctCommand);
//    }

    private void deleteFile(String command, DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter file name:");
        command += " " + scanner.nextLine();

        output.writeUTF(command);
        System.out.println("The request was sent.");
        String receivedMsg = input.readUTF();

        System.out.println(receivedMsg);

        if (receivedMsg.substring(0, 3).matches("200")) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private void addFile(String command, DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter file name:");
        command += " " + scanner.nextLine();
        System.out.println("Enter file content:");
        command += " " + scanner.nextLine();

        output.writeUTF(command);
        System.out.println("The request was sent.");
        String receivedMsg = input.readUTF();

        if (receivedMsg.substring(0, 3).matches("200")) {
            System.out.println("The response says that file was created!");
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private void getFile(String command, DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter filename:");
        command += " " + scanner.nextLine();
        output.writeUTF(command);
        System.out.println("The request was sent.");
        String receivedMsg = input.readUTF();

        if (receivedMsg.substring(0, 3).matches("404")) {
            System.out.println("The response says that the file was not found!");
        } else if (receivedMsg.substring(0, 3).matches("200")) {
            System.out.println("The content of the file is: " + receivedMsg.substring(4));
        }
    }
}
