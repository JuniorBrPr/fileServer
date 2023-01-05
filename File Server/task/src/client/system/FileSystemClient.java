package client.system;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

public class FileSystemClient {
    private final Scanner scanner = new Scanner(System.in);
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;
    private final String ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\File Server\\task\\src\\client\\data";

    public FileSystemClient() {
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
                            getFile(input, output);
                            correctCommand = true;
                        }
                        case '2' -> {
                            addFile(input, output);
                            correctCommand = true;
                        }
                        case '3' -> {
//                            deleteFile(input, output);
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

//    private void deleteFile(DataInputStream input, DataOutputStream output) throws IOException {
//        String command;
//
//        System.out.println("Enter file name:");
//        command += " " + scanner.nextLine();
//
//        output.writeUTF(command);
//        System.out.println("The request was sent.");
//        String receivedMsg = input.readUTF();
//
//        System.out.println(receivedMsg);
//
//        if (receivedMsg.substring(0, 3).matches("200")) {
//            System.out.println("The response says that the file was successfully deleted!");
//        } else {
//            System.out.println("The response says that the file was not found!");
//        }
//    }

    private void addFile(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Enter file name:");
        String filename = scanner.nextLine();

        File file = new File(ROOT + "\\" + filename);

        if (file.exists()) {
            output.writeUTF("2");
            output.writeUTF(filename);
            byte[] data = Files.readAllBytes(file.toPath());

            output.writeInt(data.length);
            output.write(data);

            System.out.println("The request was sent.");

            String receivedMsg = input.readUTF();
            System.out.println(receivedMsg);

            if (receivedMsg.substring(0, 3).matches("200")) {
                System.out.println("The response says that the file was successfully created!");
            } else {
                System.out.println("The response says that the file already exists!");
            }
        } else {
            System.out.println("The file does not exist!");
        }
//
//        System.out.println("The request was sent.");
//        String receivedMsg = input.readUTF();
//
//        if (receivedMsg.substring(0, 3).matches("200")) {
//            System.out.println("The response says that file was created!");
//        } else {
//            System.out.println("The response says that creating the file was forbidden!");
//        }
    }

    private void getFile(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id)");
        String command = scanner.nextLine();

        if (Objects.equals(command, "1")) {
            output.writeInt(1);
            System.out.println("Enter filename:");
            String filename = scanner.nextLine();
            output.writeUTF(filename);
        } else {
            output.writeInt(2);
            System.out.println("Enter id:");
            int id = scanner.nextInt();
            output.writeInt(id);
        }

        System.out.println("The request was sent.");

        byte[] message = command.getBytes();

        output.writeInt(message.length);
        output.write(message);

        System.out.println("The request was sent.");

        int length = input.readInt();
        byte[] msg = new byte[length];
        input.readFully(msg, 0, msg.length);

        System.out.println(new String(msg));
//        String receivedMsg = input.readUTF();
//
//        if (receivedMsg.substring(0, 3).matches("404")) {
//            System.out.println("The response says that the file was not found!");
//        } else if (receivedMsg.substring(0, 3).matches("200")) {
//            System.out.println("The content of the file is: " + receivedMsg.substring(4));
//        }
    }

    /**
     * Saves a file to user storage.
     *
     * @param fileName the name of the file
     * @param data     the data of the file
     * @return true if the file was saved successfully, false otherwise
     */
    protected boolean saveFile(String fileName, byte[] data) {
        try {
            Path path = Paths.get(ROOT + "/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                fos.write(data, 0, data.length);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error while saving file" + e.getMessage());
        }
        return false;
    }
}
