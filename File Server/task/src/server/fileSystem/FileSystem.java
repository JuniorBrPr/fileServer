package server.fileSystem;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class FileSystem {
    private HashMap<String, String> files;
    private final Path ROOT = ""; //Insert the path to the root folder of the file system
    private boolean exit = false;

    public FileSystem() {
        this.files = new HashMap<>();
        loadFiles();
    }

    protected String deleteFile(String fileName) {
        if (files.containsKey(fileName.strip())) {
            try {
                Files.delete(Paths.get(ROOT + "/" + fileName.strip()));
                files.remove(fileName.strip());
                return "200";
            } catch (Exception e) {
                System.out.println("Error while deleting file" + e.getMessage());
                return "404";
            }
        } else {
            return "404";
        }
    }

    protected String getFile(String fileName) {
        if (files.containsKey(fileName.strip())) {
            return "200 " + new String(getFileData(fileName.strip()));
        }
        return "404";
    }

    protected String addFile(String fileName, byte[] data) {
        loadFiles();
        if (files.containsKey(fileName.strip())) {
            return "403";
        }

        String path = saveFile(fileName.strip(), data);

        if ("Error while saving file".matches(path)) {
            return "500";
        } else if ("Success".matches(path)) {
            return "200";
        }
        return "500";
    }

    protected String saveFile(String fileName, byte[] data) {
        try {
            Path path = Paths.get(ROOT + "/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                fos.write(data, 0, data.length);
                files.put(fileName.strip(), path.toString());
                return "Success";
            }
        } catch (Exception e) {
            System.out.println("Error while saving file" + e.getMessage());
            return "Error while saving file";
        }
    }

    protected void loadFiles() {
        this.files = new HashMap<>();
        try (Stream<Path> paths = Files.walk(ROOT)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> files.put(file.getFileName().toString(), file.toFile().getPath()));
        } catch (Exception e) {
            System.out.println("Error while loading files: " + e.getMessage());
        }
    }

    protected byte[] getFileData(String fileName) {
        try {
            return Files.readAllBytes(Paths.get(ROOT + "/" + fileName ));
        } catch (Exception e) {
            System.out.println("Error while getting file data: " + e.getMessage());
            return new byte[]{};
        }
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}

