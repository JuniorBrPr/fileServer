package client.system;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class FileSystemClient {
    private final Scanner scanner = new Scanner(System.in);
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;
    private final String ROOT = "C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\File Server\\task\\src\\client\\data";

    public FileSystemClient() {
        String command = "";
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        do {
            command = "";
            System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
            command = scanner.nextLine();
            if ("exit".matches(command)) {
                try (
                        Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    output.writeUTF(command);
                    System.out.println("The request was sent.");
                    socket.close();
                    break;
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (command.length() != 0) {
                switch (command.charAt(0)) {
                    case '1' -> {
                        GET();
//                            return;
                    }
                    case '2' -> {
                        PUT();
//                            return;
                    }
                    case '3' -> {
                        DELETE();
                    }
                    default -> System.out.println("Incorrect command!");
                }
            }
        } while (!command.equals("exit"));
    }

    private void DELETE() {
        try (
                Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            requestFile(input, output, "3", "delete");

            int responseCode = input.readInt();
            switch (responseCode) {
                case 200 -> System.out.println("The response says that this file was deleted successfully!");
                case 404 -> System.out.println("The response says that this file is not found!");
                case 500 -> System.out.println("The file was not deleted. Something went wrong on the server.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void PUT() {
        System.out.println("Enter file name:");
        String filename = scanner.nextLine();

        File file = new File(ROOT + "\\" + filename);

        if (file.exists()) {
            try (
                    Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {
                output.writeUTF("2");
                output.writeUTF(filename);
                byte[] data = Files.readAllBytes(file.toPath());

                output.writeInt(data.length);
                output.write(data);

                System.out.println("The request was sent.");

                String receivedMsg = input.readUTF();

                if (receivedMsg.substring(0, 3).matches("200")) {
                    System.out.println("Response says that file is saved! ID = "+ receivedMsg.substring(4));
                } else {
                    System.out.println("The response says that the file already exists!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("The file does not exist!");
        }
    }

    private void GET() {
        try (
                Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            requestFile(input, output, "1", "get");

            int responseCode = input.readInt();

            switch (responseCode) {
                case 200 -> {
                    int length = input.readInt();
                    byte[] data = new byte[length];
                    input.readFully(data, 0, length);

                    System.out.println("The file was downloaded! Specify a name for it:");

                /*
                @TODO: add a check for the existence of a file with the same name
                @TODO: Find out why it doesn't work unless checking is filename.isEmpty()
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void requestFile(DataInputStream input, DataOutputStream output, String option, String requestType)
            throws IOException {
        System.out.println("Do you want to " + requestType + " the file by name or by id (1 - name, 2 - id)");

        try {
            output.writeUTF(option);
            int command = scanner.nextInt();

            if (command == 1) {
                System.out.println("Enter filename:");

                //@TODO: Find out why it doesn't work unless checking is filename.isEmpty()
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
    }
}
