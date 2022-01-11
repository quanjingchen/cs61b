package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Map;


/** Directory structures mapping names to references to blobs and other trees (subdirectories). */
public class GitTree implements Serializable {
    /** Folder that saves commit objects. */
    static final File OBJECTS_FOLDER = Utils.join(Repository.GITLET_DIR, "objects");

    /** A table to save files in staging area. */
    private Map<String, String> table;
    public GitTree(Map<String, String> map) {
        this.table = map;
    }

    public String getSHA1() {
        byte[] content = Utils.serialize(this);
        String outFileName = Utils.sha1(content);
        return outFileName;
    }

    public Map<String, String> getTable() {
        return table;
    }

    /** Save a GitTree to object folder */
    public void saveGitTree() {
        File outFile = new File(OBJECTS_FOLDER, getSHA1());
        Utils.writeObject(outFile, this);
    }

    /** Read a GitTree from object folder */
    public static GitTree readGitTree(String name) {
        File inFile = new File(OBJECTS_FOLDER, name);
        GitTree s = Utils.readObject(inFile, GitTree.class);
        return s;
    }

}
