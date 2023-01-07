package client;

import client.system.FileSystemClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileSystemClient client = new FileSystemClient();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
