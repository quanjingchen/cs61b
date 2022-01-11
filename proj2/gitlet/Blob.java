package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**  @Quanjingchen
 * Blobs are the saved contents of files.
 */
public class Blob implements Serializable {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** Folder that saves blob objects. */
    static final File OBJECTS_FOLDER = Utils.join(Repository.GITLET_DIR, "objects");


    /** the content of the blob*/
    public String fileName;
    public String fileContent;

    public Blob(String fileName) {
        this.fileName = fileName;
        File inFile = new File(CWD, fileName);
        if (!inFile.exists()) {
            System.out.println("file does not exist");
            System.exit(0);
        }
        this.fileContent = Utils.readContentsAsString(inFile);
    }

    /** get sha1 of the commit */
    public String getSHA1() {
        byte[] content = Utils.serialize(this);
        String outFileName = Utils.sha1(content);
        return outFileName;
    }

    /** Save a commit to object folder */
    public void saveBlob() {
        File outFile = new File(OBJECTS_FOLDER, getSHA1());
        if (!outFile.exists()) {
            Utils.writeObject(outFile, this);
        }
    }

    /** Read a commit from object folder */
    public static Blob readBlob(String name) {
        File inFile = new File(OBJECTS_FOLDER, name);
        if (!inFile.exists()) {
            System.out.println("Blob does not exist");
            System.exit(0);
        }
        Blob b = Utils.readObject(inFile, Blob.class);
        return b;
    }

    /** Write a commit back to working directory */
    public void writeBlobToFile() throws IOException {
        File newFile = new File(CWD, fileName);
        if (newFile.exists()) {
            newFile.delete();
        }
        newFile.createNewFile();
        Utils.writeContents(newFile, fileContent);
    }
}
