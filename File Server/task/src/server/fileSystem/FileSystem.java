package server.fileSystem;

import java.util.Arrays;
import java.util.Scanner;

public class FileSystem {
    private boolean[] files = new boolean[10];

    public FileSystem() {
        Arrays.fill(files, false);

        menu();
    }

    private void menu() {
        Scanner in = new Scanner(System.in);
        String userInput = "";
        do {
            userInput = in.nextLine();
            if (!userInput.matches("exit")) {
                try {
                    int file = Integer.parseInt(userInput.split(" ")[1].split("file")[1]) - 1;
                    switch (userInput.split(" ")[0]) {
                        case "add" -> {
                            if (file < 0 || file > 9 || getFile(file)) {
                                System.out.println("Cannot add the file " + userInput.split(" ")[1]);
                            } else {
                                addFile(file);
                                System.out.println("The file " + userInput.split(" ")[1] + " added successfully");
                            }
                        }
                        case "delete" -> {
                            if (file < 0 || file > files.length) {
                                System.out.println("The file " + userInput.split(" ")[1] + " not found");
                            } else if (!getFile(file)) {
                                System.out.println("The file " + userInput.split(" ")[1] + " not found");
                            } else {
                                deleteFile(file);
                                System.out.println("The file " + userInput.split(" ")[1] + " was deleted");
                            }
                        }
                        case "get" -> {
                            if (file < 0 || file > files.length || !getFile(file)) {
                                System.out.println("The file " + userInput.split(" ")[1] + " not found");
                            } else {
                                System.out.println("The file " + userInput.split(" ")[1] + " was sent");
                            }
                        }
                        default -> System.out.println("Invalid command");
                    }
                } catch (NumberFormatException e) {
                    switch (userInput.split(" ")[0]) {
                        case "add" -> System.out.println("Cannot add the file " + userInput.split(" ")[1]);
                        case "delete", "get" -> System.out.println("The file " + userInput.split(" ")[1] +
                                " not found");
                    }
                }
            }
        } while (!userInput.equals("exit"));
    }

    //Add a file to the file system
    private void addFile(int fileNumber) {
        files[fileNumber] = true;
    }

    //Delete a file from the file system
    private void deleteFile(int fileNumber) {
        files[fileNumber] = false;
    }

    //Get file from the file system
    private boolean getFile(int fileNumber) {
        return files[fileNumber];
    }
}
