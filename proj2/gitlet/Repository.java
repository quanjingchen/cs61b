package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.join;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @quanjingchen@gmail.com
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The object folder to save blobs and gitTrees etc. */
    public static final File Objects_FOLDER = join(GITLET_DIR, "objects");

    /** The object folder to save commits. */
    public static final File Commits_FOLDER = join(GITLET_DIR, "commits");

    /** The branch folder to save master and branch. */
    public static final File Branch_FOLDER = join(GITLET_DIR, "refs");

    /** The master file to save HEAD pointer in the branch dir. */
    public static final File masterFile = join(Branch_FOLDER, "master");

    /** The head pointer file. */
    public static final File ActiveBranchFile = new File(GITLET_DIR, "HEAD.txt");

    /** The index file for staging area. */
    public static final File stageFile = new File(GITLET_DIR, "index");

    /** The log file for debugging purpose. */
    public static final File logFile = new File(GITLET_DIR, "log.txt");

    /**
     * Creates a new Gitlet version-control system in the current directory. It has a single bran: Master,
     * which initially points to the initial commit
     */
    public static void init() throws IOException {

        // initialize dirs and files.
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            Objects_FOLDER.mkdir();
            Commits_FOLDER.mkdir();
            Branch_FOLDER.mkdir();
            masterFile.createNewFile();
            ActiveBranchFile.createNewFile();
            String ActiveBranch = "refs/master";
            Utils.writeContents(ActiveBranchFile, ActiveBranch);
            Commit c = new Commit();
            c.saveCommit();
            File headPath = new File(GITLET_DIR, ActiveBranch);
            Utils.writeContents(headPath, c.getSHA1());
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    /**  Adds a copy of the file as it currently exists to the staging area. */
    public static void add(String fileName) throws IOException {

        blob newFile = new blob(fileName);

        // get the head commit gitTree.
        Commit C = Commit.readCommit(getHeadCommitSha1());
        GitTree Stage = C.getGitTree();

        // build the staging area.
        Map<String, String> stageTable;
        if (!stageFile.exists() && Stage == null) {
            stageTable = new TreeMap<>();
        } else if (!stageFile.exists() && Stage != null) {
            stageTable = Stage.getTable();
        } else {
            GitTree stageTree = Utils.readObject(stageFile, GitTree.class);
            stageTable = stageTree.getTable();
        }

        // push the file into staging area and save it as a blob to the object folder.
        if (!(stageTable.containsValue(newFile.getSHA1()))) {
            stageTable.put(fileName, newFile.getSHA1());
            newFile.saveBlob();
        }
        GitTree stageTree = new GitTree(stageTable);
        Utils.writeObject(stageFile, stageTree);
    }

    /** Saves a snapshot of tracked files in the current commit and staging area
     * so they can be restored at a later time, creating a new commit. */
    public static void commit(String message) {
        // abort if no files have been staged.
        if (!stageFile.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // get the stage GitTree and save it to the object folder.
        GitTree stageTree = Utils.readObject(stageFile, GitTree.class);
        stageTree.saveGitTree();

        // get the head commit.
        String headCommit = getHeadCommitSha1();

        // push the stage file to commit, save the commit file to the object folder, and delete the stage file.
        Commit c = new Commit(message, headCommit, stageTree.getSHA1());
        c.saveCommit();
        stageFile.delete();

        // reset the head to the current commit.
        Utils.writeContents(getHeadPath(), c.getSHA1());
    }

    /** */
    public static void checkoutBranch (String branch) {
        return;
    }

    /** Takes the version of the file as it exists in the head commit and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. */
    public static void checkoutHead (String fileName) throws IOException {
        // get the head commit gitTree.
        checkoutFile(getHeadCommitSha1(), fileName);
    }

    /** Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. */
    public static void checkoutFile (String commitID, String fileName) throws IOException {
        // get the commit gitTree.
        Commit C = Commit.readCommit(commitID);
        GitTree headTree = C.getGitTree();
        Map<String, String> headTable = headTree.getTable();

        // check if the file exists in the commit gitTree.
        if (!headTable.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        // replace the file in working dir.
        blob fileBlob = blob.readBlob(headTable.get(fileName));
        fileBlob.WriteBlobToFile();
    }

    /** Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits. */
    public static void log() throws IOException {
        // get the commit gitTree.
        Commit headCommit = Commit.readCommit(getHeadCommitSha1());
        Commit tmp;
        tmp = headCommit;
        logFile.delete();
        logFile.createNewFile();
        String curString = tmp.toString();
        while (tmp.getParent() != null) {
            tmp = Commit.readCommit(tmp.getParent());
            curString += tmp.toString();
        }
        System.out.println(curString);
        Utils.writeContents(logFile, curString);
    }


    public static String getHeadCommitSha1() {
        String headCommit = Utils.readContentsAsString(getHeadPath());
        return  headCommit;
    }

    public static File getHeadPath() {
        String ActiveBranch = Utils.readContentsAsString(ActiveBranchFile);
        File headPath = new File(GITLET_DIR, ActiveBranch);
        return headPath;
    }


}



