import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * DbRow is the class who's taken the RDN to classify the images
 */
public class DbRow implements MyFile {
    public String dbPath;
    public byte[] dbByte;


    /**
     * @param dbPath
     * This following function got the RDN .pb files
     */
    public DbRow(String dbPath) {
        this.dbPath = dbPath;
        this.fileToBytes(dbPath);
    }

    public String getDbPath() {
        return dbPath;
    }


    /**
     * @param path
     * This fucntion convert .pb files to byte files
     */
    public void fileToBytes(String path) {
        try {
            this.dbByte = Files.readAllBytes(new File(path).toPath());
        } catch (IOException e) {
        }
    }

}
