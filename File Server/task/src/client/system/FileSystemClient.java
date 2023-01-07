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
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public FileSystemClient() throws IOException, InterruptedException {
        connect();

        String command = "";

        sleep(2000);

//        do {
            System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
            command = scanner.nextLine();
            if ("exit".matches(command)) {
                out.writeUTF(command);
                System.out.println("The request was sent.");
                System.exit(1);
            } else if (command.length() != 0) {
                switch (command.charAt(0)) {
                    case '1' -> GET();
                    case '2' -> PUT();
                    case '3' -> DELETE();
                    default -> System.out.println("Incorrect command!");
                }
            }
//        } while (!command.equals("exit"));
    }

    private void connect() throws InterruptedException {
        try {
            this.socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error while connecting to server: " + e.getMessage());
            sleep(1000);
            connect();
        }
    }

    private void DELETE() throws IOException {
        requestFile(in, out, "3", "delete");
        int responseCode = in.readInt();
        switch (responseCode) {
            case 200 -> System.out.println("The response says that this file was deleted successfully!");
            case 404 -> System.out.println("The response says that this file is not found!");
            case 500 -> System.out.println("The file was not deleted. Something went wrong on the server.");
        }
    }


    private void PUT() throws IOException {
        System.out.println("Enter file name:");
        String filenameOriginal = scanner.nextLine();
        File file = new File(ROOT + "\\" + filenameOriginal);

        if (file.exists()) {
            System.out.println("Enter name of the file to be saved on server:");
            String filenameServer = scanner.nextLine();

            if (filenameServer.length() == 0) {
                filenameServer = filenameOriginal;
            }

            out.writeUTF("2");
            out.writeUTF(filenameServer);
            byte[] data = Files.readAllBytes(file.toPath());

            out.writeInt(data.length);
            out.write(data);

            System.out.println("The request was sent.");

            String receivedMsg = in.readUTF();

            if (receivedMsg.substring(0, 3).matches("200")) {
                System.out.println("Response says that file is saved! ID = " + Integer.valueOf(receivedMsg.substring(4)));
            } else {
                System.out.println("The response says that the file already exists!");
            }
        } else {
            System.out.println("The file does not exist!");
        }
    }

    private void GET() throws IOException {
        requestFile(in, out, "1", "get");

        int responseCode = in.readInt();

        switch (responseCode) {
            case 200 -> {
                int length = in.readInt();
                byte[] data = new byte[length];
                in.readFully(data, 0, length);

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
            case 404 -> System.out.println("The response says that this file is not found!");
            case 403 -> System.out.println("Something went wrong on the server!");
            default -> System.out.println("Unknown response code!");
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
