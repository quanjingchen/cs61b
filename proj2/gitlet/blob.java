package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**  @Quanjingchen
 * Blobs are the saved contents of files.
 */
public class blob implements Serializable {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** Folder that saves blob objects. */
    static final File Objects_FOLDER = Utils.join(Repository.GITLET_DIR, "objects");


    /** the content of the blob*/
    public String FileName;
    public String FileContent;

    public blob(String fileName) {
        this.FileName = fileName;
        File inFile = new File(CWD, fileName);
        if (!inFile.exists()) {
            System.out.println("file does not exist");
            System.exit(0);
        }
        this.FileContent = Utils.readContentsAsString(inFile);
    }

    /** get sha1 of the commit */
    public String getSHA1() {
        byte[] content = Utils.serialize(this);
        String outFileName = Utils.sha1(content);
        return outFileName;
    }

    /** Save a commit to object folder */
    public void saveBlob() {
        File outFile = new File(Objects_FOLDER, getSHA1());
        if (!outFile.exists()) {
            Utils.writeObject(outFile, this);
        }
    }

    /** Read a commit from object folder */
    public static blob readBlob(String name) {
        File inFile = new File(Objects_FOLDER, name);
        if (!inFile.exists()) {
            System.out.println("Blob does not exist");
            System.exit(0);
        }
        blob b = Utils.readObject(inFile, blob.class);
        return b;
    }

    /** Write a commit back to working directory */
    public void WriteBlobToFile() throws IOException {
        File newFile = new File(CWD, FileName);
        if (newFile.exists()) {
            newFile.delete();
        }
        newFile.createNewFile();
        Utils.writeContents(newFile, FileContent);
    }
}
