package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.*;

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
    private String SecondParent;
    private String Tree;


    public Commit() {
        this("initial commit", null, null);
        Date date = new Date(0);
        // display time and date
        String str = String.format("%ta %<tb %<td %<tT %<tY %<tz", date);
        this.Timestamp = str;
    }
    public Commit(String message, String parent, String tree) {
        this(message, parent, null, tree);
    }

    public Commit(String message, String parent, String secondParent, String tree) {
        this.Message = message;
        this.Parent = parent;
        this.SecondParent = secondParent;
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

    public Map<String, String> getTable() {
        if (Tree != null) {
            GitTree gitTree = getGitTree();
            return gitTree.getTable();
        } else {
            return new TreeMap<>();
        }
    }

    /** get parent of the commit */
    public String getParent() {
        if (Parent != null) {
            return Parent;
        } else {
            return null;
        }
    }

    /** get message of the commit */
    public String getMessage() {
        if (Message != null) {
            return Message;
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
        // get all commits in the Commits_FOLDER
        List<String> CommitNames = Utils.plainFilenamesIn(Commits_FOLDER);
        if (name.length() < 40) {
            for (String CommitName : CommitNames) {
                String tmp = CommitName.substring(0, name.length());
                if (tmp.equals(name)) {
                    name = CommitName;
                    break;
                }
            }
        }
        File inFile = new File(Commits_FOLDER, name);
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
        if (SecondParent == null) {
            return String.format("===\ncommit %s\nDate: %s\n%s\n\n", getSHA1(), Timestamp, Message);
        } else {
            return String.format("===\ncommit %s\nMerge: %s %s \nDate: %s\n%s\n\n", getSHA1(), Parent.substring(0, 7), SecondParent.substring(0, 7), Timestamp, Message);
        }
    }


}
