package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
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
    static final File Commits_FOLDER = Utils.join(Repository.GITLET_DIR, "commits");

    /** The message of this Commit. */
    private String Message;
    private String Timestamp;
    private String Parent;
    private String Tree;


    public Commit() {
        this.Message = "initial commit";
        this.Parent = null;
        this.Tree = null;
        Date date = new Date(0);
        // display time and date
        String str = String.format("%ta %<tb %<td %<tT %<tY %<tz", date);
        this.Timestamp = str;
    }
    public Commit(String message, String parent, String tree) {
        this.Message = message;
        this.Parent = parent;
        this.Tree = tree;
        Date date = new Date();
        // display time and date
        String str = String.format("%ta %<tb %<td %<tT %<tY %<tz", date);
        this.Timestamp = str;
    }

    /** get sha1 of the commit */
    public String getSHA1() {
        byte[] content = Utils.serialize(this);
        String outFileName = Utils.sha1(content);
        return outFileName;
    }

    /** get GitTree of the commit */
    public GitTree getGitTree() {
        if (Tree != null) {
            return GitTree.readGitTree(Tree);
        } else {
            return null;
        }
    }

    /** get GitTree of the commit */
    public String getParent() {
        if (Parent != null) {
            return Parent;
        } else {
            return null;
        }
    }


    /** Save a commit to object folder */
    public void saveCommit() {
        File outFile = new File(Commits_FOLDER, getSHA1());
        Utils.writeObject(outFile, this);
    }

    /** Read a commit from object folder */
    public static Commit readCommit(String name) {
        File inFile = new File(Commits_FOLDER, name);
        if (!inFile.exists()) {
            System.out.println("Commit does not exits");
            System.exit(0);
        }
        Commit c = Utils.readObject(inFile, Commit.class);
        return c;
    }

    /** to String */
    @Override
    public String toString() {
        return String.format("===\ncommit %s\nDate: %s\n%s\n\n", getSHA1(), Timestamp, Message);
    }



}
