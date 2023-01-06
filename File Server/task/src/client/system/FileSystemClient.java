package client.system;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
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
                            GET(input, output);
                            correctCommand = true;
                            socket.close();
                        }
                        case '2' -> {
                            PUT(input, output);
                            correctCommand = true;
                            socket.close();
                        }
                        case '3' -> {
//                            deleteFile(input, output);
                            correctCommand = true;
                            socket.close();
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

    private void PUT(DataInputStream input, DataOutputStream output) throws IOException {
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

    private void GET(DataInputStream input, DataOutputStream output) throws IOException {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id)");

        try {
            output.writeUTF("1");
            int command = scanner.nextInt();

            if (command == 1) {
                System.out.println("Enter filename:");
                String filename = scanner.nextLine();
                if (filename.isEmpty()) {
                    filename = scanner.nextLine();
                }
                output.writeInt(1);
                output.writeUTF(filename);
            } else if (command == 2) {
                output.writeInt(2);
                System.out.println("Enter id:");
                int id = scanner.nextInt();
                output.writeInt(id);
            } else {
                System.out.println("Incorrect command!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Incorrect command, please enter a number!");
        }

        System.out.println("The request was sent.");

        int responseCode = input.readInt();

        switch (responseCode) {
            case 200 -> {
                int length = input.readInt();
                byte[] data = new byte[length];
                input.readFully(data, 0, length);

                System.out.println("The file was downloaded! Specify a name for it:");

                /*@TODO: add a check for the existence of a file with the same name
                 *@TODO: Find out why it doesn't work unless checking is filename.isEmpty()
                 */
                String filename = scanner.nextLine();
                if (filename.isEmpty()) {
                    filename = scanner.nextLine();
                }

                try {
                    Path path = Paths.get(ROOT + "\\" + filename);
                    Files.write(path, data);

                    System.out.println("File saved on the hard drive!");
                } catch (Exception e) {
                    System.out.println("Error saving file!: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case 404 -> System.out.println("The file was not found!");
            case 403 -> System.out.println("Something went wrong on the server!");
            default -> System.out.println("Unknown response code!");
        }
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
