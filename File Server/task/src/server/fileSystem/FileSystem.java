package server.fileSystem;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.stream.Stream;

class FileSystem {
    private volatile Hashtable<String, String> files;
    private volatile Hashtable<String, String> ids;
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
    protected String DELETE(String identifier, String identifierType) {
        try {
            if (identifierType.equals("id")) {
                if (ids.containsKey(identifier)) {
                    String fileName = ids.get(identifier);
                    ids.remove(identifier);
                    files.remove(fileName);
                    Files.deleteIfExists(Paths.get(ROOT + "/" + fileName));
                    return "200";
                }
            } else if (identifierType.equals("name")) {
                if (files.containsKey(identifier)) {
                    ids.forEach((key, value) -> {
                        if (value.equals(identifier)) {
                            ids.remove(key);
                        }
                    });
                    files.remove(identifier);
                    Files.deleteIfExists(Paths.get(ROOT + "/" + identifier));
                    return "200";
                }
            }
        } catch (Exception e) {
            System.out.println("Error while deleting file: " + e.getMessage());
            e.printStackTrace();
        }
        return "404";
    }

    /**
     * Get file data from file name or ID
     *
     * @param identifier     The name or ID of the file
     * @param identifierType The type of identifier, either "name" or "id"
     * @return The file data
     */
    protected byte[] GET(String identifier, String identifierType) {
        if (identifierType.equals("name")) {
            if (files.containsKey(identifier.strip())) {
                return getFileData(identifier.strip());
            }
        } else if (identifierType.equals("id")) {
            if (ids.containsKey(identifier.strip())) {
                return getFileData(ids.get(identifier.strip()));
            }
        }
        return "404".getBytes();
    }

    /**
     * Add a file to the server.
     *
     * @param fileName the name of the file
     * @param data     the data of the file
     * @return 200 and the file ID if the file was created, 403 if the file already exists and 500 if an error occurred.
     */
    protected String PUT(String fileName, byte[] data) {
        if (files.containsKey(fileName.strip())) {
            return "403";
        }

        String id = saveFile(fileName.strip(), data);

        return id.matches("Error while saving file") ? "500" : "200 " + id;
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
                files.put(fileName.strip(), path.toString());
                ids.put(id, fileName.strip());
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
        this.ids = new Hashtable<>();
        try (Stream<Path> paths = Files.walk(ROOT)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        files.put(file.getFileName().toString(), file.toFile().getPath());
                        ids.put(generateId(), file.getFileName().toString());
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
        int ID_LENGTH = 10;
        int ID_SYMBOL_RANGE = 10; //Max range: 36
        String code = "";

        char[] codeArray = new char[ID_LENGTH];
        HashSet<Character> set = new HashSet<>();
        int i = 0;
        do {
            int random = (int) (Math.random() * ID_SYMBOL_RANGE);
            char c = (char) (random < 10 ? random + '0' : random + 'a' - 10);
            if (set.add(c)) {
                codeArray[i] = c;
                i++;
            }
        } while (set.size() < ID_LENGTH);

        code = new String(codeArray);

        return this.ids.containsKey(code) ? generateId() : code;
    }

    public boolean isExit() {
        return exit;
    }

    public synchronized void setExit(boolean exit) {
        this.exit = exit;
    }
}

