package server.fileSystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

class FileSystem {
    private volatile Hashtable<String, String> files;
    private final Path ROOT = Paths.get("C:\\Users\\junio\\OneDrive\\Bureaublad\\OOP1\\File Server\\File Server\\task\\src\\server\\data");
    private volatile boolean exit = false;

    public FileSystem() {
        loadFiles();
    }

    /**
     * Delete a file from the server.
     *
     * @param identifier     the identifier of the file
     * @param identifierType the type of the identifier either "name" or "id"
     * @return Status code
     */
//    protected String DELETE(String identifier, String identifierType) {
//        try {
//            if (identifierType.equals("id")) {
//                if (ids.containsKey(identifier)) {
//                    String fileName = ids.get(identifier);
//                    ids.remove(identifier);
//                    files.remove(fileName);
//                    Files.deleteIfExists(Paths.get(ROOT + "/" + fileName));
//                    return "200";
//                }
//            } else if (identifierType.equals("name")) {
//                if (files.containsKey(identifier)) {
//                    ids.forEach((key, value) -> {
//                        if (value.equals(identifier)) {
//                            ids.remove(key);
//                        }
//                    });
//                    files.remove(identifier);
//                    Files.deleteIfExists(Paths.get(ROOT + "/" + identifier));
//                    return "200";
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error while deleting file: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return "404";
//    }

    /**
     * Get file data from file name or ID
     *
     * @param identifier     The name or ID of the file
     * @param identifierType The type of identifier, either "name" or "id"
     * @return The file data
     */
//    protected byte[] GET(String identifier, String identifierType) {
//        int byNameOrId = input.readInt();
//
//        String fileName = byNameOrId == 1 ? input.readUTF() : getByID(input.readInt());
//
//        if (identifierType.equals("name")) {
//            if (files.containsKey(identifier.strip())) {
//                return getFileData(identifier.strip());
//            }
//        } else if (identifierType.equals("id")) {
//            if (ids.containsKey(identifier.strip())) {
//                return getFileData(ids.get(identifier.strip()));
//            }
//        }
//        return "404".getBytes();
//    }

    /**
     * Add a file to the server.
     *
     * @param input  The input stream
     * @param output The output stream
     * @return Status code: 200 if successful, 404 if not
     */
    protected void PUT(DataInputStream input, DataOutputStream output) {
        try {
            String fileName = input.readUTF();

            if (files.containsKey(fileName.strip())) {
                output.writeUTF("403");
            }

            int fileSize = input.readInt();
            byte[] fileData = new byte[fileSize];
            input.readFully(fileData, 0, fileSize);

            String id = saveFile(fileName.strip(), fileData);

            String message;
            if (id.matches("Error while saving file")) {
                message = "500";
            } else {
                message = "200 " + id;
            }
            output.writeUTF(message);
        } catch (Exception e) {
            System.out.println("Error while saving file: " + e.getMessage());
            e.printStackTrace();
            try {
                output.writeUTF("500");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Saves a file to the server storage
     *
     * @param fileName the name of the file
     * @param data     the data of the file
     * @return the ID of the file
     */
    protected String saveFile(String fileName, byte[] data) {
        try {
            Path path = Paths.get(ROOT + "/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                String id = generateId();
                fos.write(data, 0, data.length);
                files.put(fileName.strip(), id);
                return id;
            }
        } catch (Exception e) {
            System.out.println("Error while saving file" + e.getMessage());
            return "Error while saving file";
        }
    }

    /**
     * Only used for testing purposes
     * Load all the files from the server storage and put them in a hashtable and generate an ID for each file
     */
    protected void loadFiles() {
        this.files = new Hashtable<>();
        try (Stream<Path> paths = Files.walk(ROOT)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        files.put(file.getFileName().toString(), generateId());
                    });
        } catch (Exception e) {
            System.out.println("Error while loading files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get file data from the server storage
     *
     * @param fileName the name of the file
     * @return the data of the file
     */
    protected byte[] getFileData(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(ROOT + "/" + fileName));
        } catch (Exception e) {
            System.out.println("Error while getting file data: " + e.getMessage());
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * Generate a random unique ID
     *
     * @return Unique ID
     */
    private String generateId() {
        double ID_MAX_LENGTH = 10;
        double code = Math.floor((Math.random() * Math.pow(10, ID_MAX_LENGTH)));

        if (files.contains(String.valueOf(code))) {
            return generateId();
        }

        System.out.println("Generated ID: " + code);

        return Double.toString(code);
    }

    public boolean isExit() {
        return exit;
    }

    public synchronized void setExit(boolean exit) {
        this.exit = exit;
    }

    private String getByID(int id) {
        AtomicReference<String> fileName = new AtomicReference<>("");

        files.forEach((key, value) -> {
            if (value.equals(Integer.toString(id))) {
                fileName.set(key);
            }
        });

        return Objects.equals("", fileName.get()) ? "404" : fileName.get();
    }
}

