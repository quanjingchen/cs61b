package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @Quanjingchen
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** Folder that saves commit objects. */
    static final File COMMITS_FOLDER = Utils.join(Repository.GITLET_DIR, "commits");

    /** The message of this Commit. */
    private String message;
    private String timeStamp;
    private String parent;
    private String secondParent;
    private String tree;


    public Commit() {
        this("initial commit", null, null);
        Date date = new Date(0);
        // display time and date
        String str = String.format("%ta %<tb %<td %<tT %<tY %<tz", date);
        this.timeStamp = str;
    }
    public Commit(String message, String parent, String tree) {
        this(message, parent, null, tree);
    }

    public Commit(String message, String parent, String secondParent, String tree) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.tree = tree;
        Date date = new Date();
        // display time and date
        String str = String.format("%ta %<tb %<td %<tT %<tY %<tz", date);
        this.timeStamp = str;
    }

    /** get sha1 of the commit */
    public String getSHA1() {
        byte[] content = Utils.serialize(this);
        String outFileName = Utils.sha1(content);
        return outFileName;
    }

    /** get GitTree of the commit */
    public GitTree getGitTree() {
        if (tree != null) {
            return GitTree.readGitTree(tree);
        } else {
            return null;
        }
    }

    public Map<String, String> getTable() {
        if (tree != null) {
            GitTree gitTree = getGitTree();
            return gitTree.getTable();
        } else {
            return new TreeMap<>();
        }
    }

    /** get parent of the commit */
    public String getParent() {
        if (parent != null) {
            return parent;
        } else {
            return null;
        }
    }

    /** get message of the commit */
    public String getMessage() {
        if (message != null) {
            return message;
        } else {
            return null;
        }
    }


    /** Save a commit to object folder */
    public void saveCommit() {
        File outFile = new File(COMMITS_FOLDER, getSHA1());
        Utils.writeObject(outFile, this);
    }

    /** Read a commit from object folder */
    public static Commit readCommit(String name) {
        // get all commits in the COMMITS_FOLDER
        List<String> commitNames = Utils.plainfileNamesIn(COMMITS_FOLDER);
        if (name.length() < 40) {
            for (String commitName : commitNames) {
                String tmp = commitName.substring(0, name.length());
                if (tmp.equals(name)) {
                    name = commitName;
                    break;
                }
            }
        }
        File inFile = new File(COMMITS_FOLDER, name);
        if (!inFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit c = Utils.readObject(inFile, Commit.class);
        return c;
    }

    /** convert commit to String */
    @Override
    public String toString() {
        if (secondParent == null) {
            return String.format("===\ncommit %s\nDate: %s\n%s\n\n", getSHA1(), timeStamp, message);
        } else {
            return String.format("===\ncommit %s\nMerge: %s %s \nDate: %s\n%s\n\n",
                    getSHA1(), parent.substring(0, 7), secondParent.substring(0, 7), timeStamp, message);
        }
    }


}
