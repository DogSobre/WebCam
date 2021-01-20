import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DbRow implements MyFile {
    private String dbPath;
    private byte[] dbByte;


    public DbRow(String dbPath) {
        this.dbPath = dbPath;
        this.fileToBytes(dbPath);
    }

    public String getDbPath() {
        return dbPath;
    }

    public byte[] getDbByte() {
        return dbByte;
    }

    public void fileToBytes(String path) {
        try {
            this.dbByte = Files.readAllBytes(new File(path).toPath());
        } catch (IOException e) {
        }
    }

}
