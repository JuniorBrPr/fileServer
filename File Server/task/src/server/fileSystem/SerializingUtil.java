package server.fileSystem;

import java.io.*;
import java.nio.file.Paths;

public class SerializingUtil {
    protected static void serialize(FileSystem fileSystem, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(Paths.get(path).toFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(fileSystem);
        oos.close();
    }

    protected static FileSystem deserialize(String path) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(Paths.get(path).toFile());
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        FileSystem fileSystem = (FileSystem) ois.readObject();
        ois.close();
        return fileSystem;
    }
}
