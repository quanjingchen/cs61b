package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.join;

/** Represents a gitlet repository.
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
    public static final File OBJECTS_FOLDER = join(GITLET_DIR, "objects");

    /** The object folder to save commits. */
    public static final File COMMITS_FOLDER = join(GITLET_DIR, "commits");

    /** The branch folder to save master and branch. */
    public static final File BRANCH_FOLDER = join(GITLET_DIR, "refs");

    /** The master file to save HEAD pointer in the branch dir. */
    public static final File MASTER_FILE = join(BRANCH_FOLDER, "master");

    /** The head pointer file. */
    public static final File ACTIVEBRANCH_FILE = new File(GITLET_DIR, "HEAD.txt");

    /** The index file for staging area. */
    public static final File STAGE = new File(GITLET_DIR, "index");

    /** The trash file for file removal. */
    public static final File TRASH = new File(GITLET_DIR, "trash");

    /** The log file for debugging purpose. */
    public static final File LOGFILE = new File(GITLET_DIR, "log.txt");

    /**
     * Creates a new Gitlet version-control system in the current directory.
     * It has a single bran: Master,
     * which initially points to the initial commit
     */
    public static void init() throws IOException {

        // initialize dirs and files.
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            OBJECTS_FOLDER.mkdir();
            COMMITS_FOLDER.mkdir();
            BRANCH_FOLDER.mkdir();
            MASTER_FILE.createNewFile();
            ACTIVEBRANCH_FILE.createNewFile();
            String activeBranch = "refs/master";
            Utils.writeContents(ACTIVEBRANCH_FILE, activeBranch);
            Commit c = new Commit();
            c.saveCommit();
            File headPath = new File(GITLET_DIR, activeBranch);
            Utils.writeContents(headPath, c.getSHA1());
        } else {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
        }
    }

    /**  Adds a copy of the file as it currently exists to the staging area. */
    public static void add(String fileName) throws IOException {
        Blob newFile = new Blob(fileName);
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // build the staging area.
        Map<String, String> stageTable = buildStage();
        Map<String, String> trashTable = buildTrash();
        // If the file is added it will no longer be staged for removal.
        trashTable.remove(fileName);
        Utils.writeObject(TRASH, new GitTree(trashTable));
        // If the current working version of the file is not added
        // and is not identical to the version in the current commit,
        // stage it and remove it from trash if it's there.
        if (!(stageTable.containsValue(newFile.getSHA1()))
                & !(fileTable.containsValue(newFile.getSHA1()))) {
            stageTable.put(fileName, newFile.getSHA1());
            newFile.saveBlob();
        }
        // If the current working version of the file is identical to
        // the version in the current commit,
        // do not stage it to be added,
        // and remove it from the staging area if it is already there
        if (stageTable.containsValue(newFile.getSHA1()) & fileTable.containsValue(newFile.getSHA1())) {
            stageTable.remove(fileName);
        }
        GitTree stageTree = new GitTree(stageTable);
        Utils.writeObject(STAGE, stageTree);
    }

    /**  Remove a file from the staging area and the working directory if it's tracked. */
    public static void rm(String fileName) throws IOException {
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // remove the file from the working directory
        // if it's tracked in the current commit.
        if (fileTable.containsKey(fileName)) {
            File newFile = new File(CWD, fileName);
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        // load the staging area.
        Map<String, String> stageTable = buildStage();
        // build a trash
        if (stageTable.containsKey(fileName) || fileTable.containsKey(fileName)) {
            // remove the file from staging area if it's staged.
            if (stageTable.containsKey(fileName)) {
                stageTable.remove(fileName);
                Utils.writeObject(STAGE, new GitTree(stageTable));
            } else {
                // load the trash.
                Map<String, String> trashTable = buildTrash();
                // write file name to trash table if it's staged or tracked.
                trashTable.put(fileName, "to be deleted");
                Utils.writeObject(TRASH, new GitTree(trashTable));
            }
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /** load the existing stage area or build a new one. */
    public static Map<String, String> buildStage() {
        // build the staging area.
        Map<String, String> stageTable;
        if (!STAGE.exists()) {
            stageTable = new TreeMap<>();
        } else {
            GitTree stageTree = Utils.readObject(STAGE, GitTree.class);
            stageTable = stageTree.getTable();
        }
        return stageTable;
    }

    /** load the existing trash or build a new one. */
    public static Map<String, String> buildTrash() {
        // build the staging area.
        Map<String, String> trashTable;
        if (!TRASH.exists()) {
            trashTable = new TreeMap<>();
        } else {
            GitTree trashTree = Utils.readObject(TRASH, GitTree.class);
            trashTable = trashTree.getTable();
        }
        return trashTable;
    }


    /** Saves a snapshot of tracked files in the current commit and staging area,
     * so they can be restored at a later time, creating a new commit. */
    public static void commit(String message) {
        commit(message, null);
    }
    public static void commit(String message, String secondParent) {
        // abort if no files have been staged.
        if (!STAGE.exists() & !TRASH.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // get the stage GitTree and add them to the file table.
        if (STAGE.exists()) {
            GitTree stageTree = Utils.readObject(STAGE, GitTree.class);
            Map<String, String> stageTable = stageTree.getTable();
            fileTable.putAll(stageTable);
            // empty the stage area.
            STAGE.delete();
        }
        // get the trash GitTree and remove them from the file table.
        if (TRASH.exists()) {
            GitTree trashTree = Utils.readObject(TRASH, GitTree.class);
            Map<String, String> trashTable = trashTree.getTable();
            Set<String> trashKeys = trashTable.keySet();
            fileTable.keySet().removeAll(trashKeys);
            // empty the trash.
            TRASH.delete();
        }
        // save the new GitTree.
        GitTree newGitTree = new GitTree(fileTable);
        newGitTree.saveGitTree();
        // get the head commit.
        String headCommit = getheadcommitSha1();
        // push the stage file to commit, save the commit file to the object folder.
        Commit c = new Commit(message, headCommit, secondParent, newGitTree.getSHA1());
        c.saveCommit();
        // reset the head to the current commit.
        Utils.writeContents(getHeadPath(), c.getSHA1());
    }

    /** Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node. */
    public static void reset (String commitID) throws IOException {
        checkoutCommit(getheadcommitSha1(), commitID);
        // reset the head to the current commit.
        Utils.writeContents(getHeadPath(), commitID);
        // clean the stage and trash area.
        STAGE.delete();
        TRASH.delete();
    }

    /**  Creates a new branch with the given name,
     * and points it at the current head commit. */
    public static void branch (String branchName) throws IOException {
        // get current head commit SHA1
        String headcommitSha1 = getheadcommitSha1();
        // The branch file that saves HEAD pointer in the branch dir.
        File branchFile = join(BRANCH_FOLDER, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchFile.createNewFile();
        // points it at the current head commit but DON'T immediately switch to the newly created branch
        String activeBranch = "refs/" + branchName;
        File headPath = new File(GITLET_DIR, activeBranch);
        Utils.writeContents(headPath, headcommitSha1);
    }

    /** Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch,
     * or anything like that. */
    public static void rm_branch (String branchName) throws IOException {
        // The branch file that saves HEAD pointer in the branch dir.
        File branchFile = join(BRANCH_FOLDER, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // get the current active branch
        String activeBranch_path = Utils.readContentsAsString(ACTIVEBRANCH_FILE);
        String activeBranch = activeBranch_path.substring(5,activeBranch_path.length());
        // quit if the given branch is the one you’re currently working on.
        if (branchName.equals(activeBranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else {
            branchFile.delete();
        }

    }

    /** Takes all files in the given commit,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * Any files that are tracked in the current commit but are not present
     * in the checked-out commit are deleted. */
    public static void checkoutCommit (String commitID_C,String commitID_B) throws IOException {
        // get the commit of the current branch.
        Commit C = Commit.readCommit(commitID_C);
        Map<String, String> fileTable = C.getTable();
        Set<String> ckeySet = fileTable.keySet();

        // get the commit of the given commit.
        Commit B = Commit.readCommit(commitID_B);
        Map<String, String> fileTableBranch = B.getTable();
        Set<String> bkeySet = fileTableBranch.keySet();

        List<String> fileNames = Utils.plainfileNamesIn(CWD);
        Set<String> cwdSet = new HashSet<>(fileNames);
        // get the intersection of the CWD and Bkey sets.
        Set<String> inter_B_CWD = new HashSet<>(cwdSet);
        inter_B_CWD.retainAll(bkeySet);
        // exclude files in Ckey set.
        inter_B_CWD.removeAll(ckeySet);
        if (!inter_B_CWD.isEmpty()) {
            // quit if a working file is untracked in the current branch and
            // would be overwritten by the checkout.
            // check this before doing anything else.
            System.out.println("There is an untracked file in the way; " +
                    "delete it, or add and commit it first.");
            System.exit(0);
        }

        // iterate over the files in the given commit.
        for (String BKey : bkeySet) {
            File fileTmp = join(CWD, BKey);
            //  overwrite the versions of the files that are tracked or create it if not exist.
                // create/replace the file in working dir.
            Blob fileBlob = Blob.readBlob(fileTableBranch.get(BKey));
            fileBlob.writeBlobToFile();
        }

        // delete the files that are tracked in the current branch and
        // are not present in the given branch
        ckeySet.removeAll(bkeySet);
        for (String CKey : ckeySet) {
            File fileTmp = join(CWD, CKey);
            fileTmp.delete();
        }

    }


    /** Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions of
     * the files that are already there if they exist.
     * Also, at the end of this command,
     * the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch but
     * are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch */
    public static void checkoutBranch (String branchName) throws IOException {

        // get the current active branch name
        String activeBranch_path = Utils.readContentsAsString(ACTIVEBRANCH_FILE);
        String activeBranch = activeBranch_path.substring(5,activeBranch_path.length());
        if (branchName.equals(activeBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkoutCommit(getheadcommitSha1(), getBranchheadcommitSha1(branchName));

        // reset the active branch, clear the stage area and trash.
        Utils.writeContents(ACTIVEBRANCH_FILE, "refs/" + branchName);
        STAGE.delete();
        TRASH.delete();
    }

    /** Takes the version of the file as it exists in the head commit and
     * puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. */
    public static void checkoutHead (String fileName) throws IOException {
        // get the head commit gitTree.
        checkoutFile(getheadcommitSha1(), fileName);
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one. */
    public static void checkoutFile (String commitID, String fileName) throws IOException {
        // get the commit of the current branch.
        Commit C = Commit.readCommit(getheadcommitSha1());
        Map<String, String> fileTable = C.getTable();
        Set<String> ckeySet = fileTable.keySet();

        // get the commit of the given commit.
        Commit B = Commit.readCommit(commitID);
        Map<String, String> fileTableB = B.getTable();
        Set<String> bkeySet = fileTableB.keySet();

        // check if the file exists in the cwd.
        File fileTmp = join(CWD, fileName);

        // check if the file exists in the commit gitTree.
        if (!fileTableB.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        // Quit if a working file is untracked in the current branch
        // and would be overwritten by the checkout.
        if (!fileTable.containsKey(fileName) & fileTmp.exists()) {
            System.out.println("There is an untracked file in the way; delete it, " +
                    "or add and commit it first.");
            System.exit(0);
        }

        // replace the file in working dir.
        Blob fileBlob = Blob.readBlob(fileTableB.get(fileName));
        fileBlob.writeBlobToFile();
    }



    /** Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits. */
    public static void log() throws IOException {
        // get the commit gitTree.
        Commit headCommit = Commit.readCommit(getheadcommitSha1());
        Commit tmp;
        tmp = headCommit;
        LOGFILE.delete();
        LOGFILE.createNewFile();
        String curString = tmp.toString();
        while (tmp.getParent() != null) {
            tmp = Commit.readCommit(tmp.getParent());
            curString += tmp.toString();
        }
        System.out.println(curString);
        Utils.writeContents(LOGFILE, curString);
    }

    /** displays information about all commits ever made regardless of the order. */
    public static void global_log() throws IOException {
        // get all commits in the COMMITS_FOLDER
        List<String> commitNames = Utils.plainfileNamesIn(COMMITS_FOLDER);
        for (String CommitName : commitNames) {
            Commit tmp = Commit.readCommit(CommitName);
            System.out.println(tmp.toString());
        }
    }

    /** Prints out the ids of all commits that have the given commit message, one per line. */
    public static void find(String message) throws IOException {
        // get all commits in the COMMITS_FOLDER
        List<String> commitNames = Utils.plainfileNamesIn(COMMITS_FOLDER);
        int n = 0;
        for (String CommitName : commitNames) {
            Commit tmp = Commit.readCommit(CommitName);
            if (tmp.getMessage().equals(message)) {
                System.out.println(CommitName);
                n ++;
            }
        }
        if (n == 0) {
            System.out.println("Found no commit with that message.");
        }
    }


        /**  Displays what branches currently exist, and marks the current branch with a *.
         * Also displays what files have been staged for addition or removal. */
    public static void status() throws IOException {
        // get all branch
        List<String> branchNames = Utils.plainfileNamesIn(BRANCH_FOLDER);
        // get current branch
        String activeBranch_path = Utils.readContentsAsString(ACTIVEBRANCH_FILE);
        String activeBranch = activeBranch_path.substring(5,activeBranch_path.length());
        System.out.println("=== Branches ===");
        // for each branch print the branch name and add * if it's the active branch.
        for (String branchName : branchNames) {
            if (branchName.equals(activeBranch)) {
                System.out.println(String.format("*%s", branchName));
            } else {
                System.out.println(branchName);
            }
        }
        // display what files have been staged
        System.out.println("\n=== Staged Files ===");
        // get the stage GitTree and print all the keys.
        if (STAGE.exists()) {
            GitTree stageTree = Utils.readObject(STAGE, GitTree.class);
            Map<String, String> stageTable = stageTree.getTable();
            Set<String> stageKeys = stageTable.keySet();
            //print all the keys
            for (String key : stageKeys) {
                System.out.println(key);
            }
        }
        // display what files have been removed
        System.out.println("\n=== Removed Files ===");
        // get the trash GitTree and print all the keys.
        if (TRASH.exists()) {
            GitTree trashTree = Utils.readObject(TRASH, GitTree.class);
            Map<String, String> trashTable = trashTree.getTable();
            Set<String> trashKeys = trashTable.keySet();
            //print all the keys
            for (String key : trashKeys) {
                System.out.println(key);
            }
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===\n");
    }

    /** Merges files from the given branch into the current branch. */
    public static void merge(String branchName) throws IOException {
        if (STAGE.exists() || TRASH.exists()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        // get the current active branch name
        String activeBranch_path = Utils.readContentsAsString(ACTIVEBRANCH_FILE);
        String activeBranch = activeBranch_path.substring(5,activeBranch_path.length());
        // can't merge a branch with itself.
        if (branchName.equals(activeBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        // No such branch exists.
        File BranchHeadPath = join(BRANCH_FOLDER, branchName);
        if (!BranchHeadPath.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // get the commit of the current branch.
        Commit C = Commit.readCommit(getheadcommitSha1());
        Map<String, String> fileTableC = C.getTable();
        Set<String> ckeySet = fileTableC.keySet();
        // get the ancestors of the current branch.
        List<String> cAncestor = getCommitAncestor(C);
        // get the commit of the given branch.
        Commit B = Commit.readCommit(getBranchheadcommitSha1(branchName));
        Map<String, String> fileTableB = B.getTable();
        Set<String> bkeySet = fileTableB.keySet();
        // get the ancestors of the given branch.
        List<String> bAncestor = getCommitAncestor(B);

        // get the split point
        int splitIndex = 0;
        for (int i = 1; i <= Math.min(cAncestor.size(), bAncestor.size()); i ++) {
            if (!cAncestor.get(cAncestor.size()-i).equals(bAncestor.get(bAncestor.size()-i))) {
                break;
            }
            splitIndex ++;
        }
        if (splitIndex == bAncestor.size()) {
            // If the split point is the same commit as the give branch,
            // then we do nothing
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitIndex == cAncestor.size()) {
            //If the split point is the current branch,
            // then check out the given branch
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        // get the commit at the split point
        Commit S = Commit.readCommit(cAncestor.get(cAncestor.size() - splitIndex));
        Map<String, String> fileTableS = S.getTable();
        Set<String> skeySet = fileTableS.keySet();
        // get the union of the three key sets
        Set<String> uni_3 = new HashSet<String>();
        uni_3.addAll(skeySet);
        uni_3.addAll(ckeySet);
        uni_3.addAll(bkeySet);
        // get the intersection of the three key sets.
        Set<String> inter_3 = new HashSet<>(skeySet);
        inter_3.retainAll(ckeySet);
        inter_3.retainAll(bkeySet);
        // get the intersection of S and C.
        Set<String> inter_SC = new HashSet<>(skeySet);
        inter_SC.retainAll(ckeySet);
        // get the intersection of S and B.
        Set<String> inter_SB = new HashSet<>(skeySet);
        inter_SB.retainAll(bkeySet);
        // get the intersection of S and C.
        Set<String> inter_CB = new HashSet<>(ckeySet);
        inter_CB.retainAll(bkeySet);

        // check if an untracked file in the current commit would be overwritten by the merge.
        // before doing anything else.
        List<String> fileNames = Utils.plainfileNamesIn(CWD);
        Set<String> cwdSet = new HashSet<>(fileNames);
        // get the intersection of the CWD and Bkey sets.
        Set<String> inter_B_CWD = new HashSet<>(cwdSet);
        inter_B_CWD.retainAll(bkeySet);
        // exclude files in Ckey set.
        inter_B_CWD.removeAll(ckeySet);
        if (!inter_B_CWD.isEmpty()) {
            for (String file : inter_B_CWD) {
                if (!skeySet.contains(file) || (skeySet.contains(file)
                        & !fileTableS.get(file).equals(fileTableB.get(file)))) {
                    System.out.println("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }


        // iterate over files
        for (String file : uni_3) {
            // if file is tracked in all the three sets.
            if (inter_3.contains(file)) {

                // 1) S = C != B : check B and stage
                if (fileTableS.get(file).equals(fileTableC.get(file))
                        & !fileTableS.get(file).equals(fileTableB.get(file))) {
                    // Any files that have been modified in the given branch (B) since the split point,
                    // but not modified in the current branch (C) since the split point
                    // should be changed to their versions in the given branch (B)
                    // (checked out from the commit at the front of the given branch).
                    checkoutFile(B.getSHA1(), file);
                    add(file);
                }
                // 2) S = B != C : stay
                // 3) S != B = C : stay
                // 4) S != C != B : conflict
                if (!fileTableS.get(file).equals(fileTableC.get(file))
                        & !fileTableS.get(file).equals(fileTableB.get(file))
                        & !fileTableC.get(file).equals(fileTableB.get(file))) {
                     // Any files modified in different ways in the current
                    // and given branches are in conflict.
                    // replace the contents of the conflicted file and stage the result.
                    mergeHelper(fileTableC.get(file),fileTableB.get(file));
                    add(file);
                }

            } else if (inter_SC.contains(file)) {
                if (fileTableS.get(file).equals(fileTableC.get(file))) {
                    // 5) S = C !B : remove and untrack
                    // Any files present at the split point,
                    // unmodified in the current branch,
                    // and absent in the given branch should be removed (and untracked).
                    rm(file);
                } else {
                    // 6) S != C !B : conflict
                    mergeHelper(fileTableC.get(file));
                }

            } else if (inter_SB.contains(file)) {
                // 7) S = B !C : remain
                // 8) S != B !C : conflict
                if (!fileTableS.get(file).equals(fileTableB.get(file))) {
                    mergeHelper(fileTableC.get(file));
                }

            } else if (inter_CB.contains(file)) {
                // 9)  !S C = B : remain
                // 10) !S C != B : conflict
                if (!fileTableC.get(file).equals(fileTableB.get(file))) {
                    // If the file was absent at the split point
                    // and has different contents in the given and current branches,
                    // replace the contents of the conflicted file and stage the result.
                    mergeHelper(fileTableC.get(file),fileTableB.get(file));
                }

                // 11) !S C !B : remain
                // 12) !S !C B : check and stage
            } else if (bkeySet.contains(file)) {
                checkoutFile(B.getSHA1(), file);
                add(file);
                // 13) S !C !B : remain
            }
        }
        String msg = "Merged " + branchName + " into " + activeBranch + ".";
        commit(msg,getBranchheadcommitSha1(branchName));
    }

    /** merge one txt files with an empty file and save to the working directory */
    public static void mergeHelper(String fileName1) throws IOException {
        mergeHelper(fileName1, null);
    }

    /** merge two txt files with the same name and
     * different gitSHA1 and save to the working directory */
    public static void mergeHelper(String fileName1, String fileName2) throws IOException {
        Blob fileBlob_C = Blob.readBlob(fileName1);
        String file_C = fileBlob_C.fileContent;
        String tmp;
        if (fileName2 != null) {
            Blob fileBlob_B = Blob.readBlob(fileName2);
            String file_B = fileBlob_B.fileContent;
            if (!fileBlob_C.fileName.equals(fileBlob_B.fileName)) {
                System.out.println("Two files must have the same file names.");
                System.exit(0);
            }
            tmp = "<<<<<<< HEAD\n" + file_C + "=======\n" + file_B + ">>>>>>>";
        } else {
            tmp = "<<<<<<< HEAD\n" + file_C + "=======\n" + ">>>>>>>";
        }
        File newFile = new File(CWD, fileBlob_C.fileName);
        newFile.createNewFile();
        Utils.writeContents(newFile, tmp);
        System.out.println("Encountered a merge conflict.");
    }

    public static List<String> getCommitAncestor(Commit C) {
        // get the commit of the split point
        List<String> cAncestor = new ArrayList<String>();
        cAncestor.add(C.getSHA1());
        while (C.getParent() != null) {
            C = Commit.readCommit(C.getParent());
            cAncestor.add(C.getSHA1());
        }
        return cAncestor;
    }

    public static Map<String, String> getHeadCommitFileTable() {
        Commit C = Commit.readCommit(getheadcommitSha1());
        return  C.getTable();
    }


    public static String getheadcommitSha1() {
        String headCommit = Utils.readContentsAsString(getHeadPath());
        return  headCommit;
    }

    public static File getHeadPath() {
        String activeBranch = Utils.readContentsAsString(ACTIVEBRANCH_FILE);
        File headPath = new File(GITLET_DIR, activeBranch);
        return headPath;
    }

    public static String getBranchheadcommitSha1(String branchName) {
        File BranchHeadPath = join(BRANCH_FOLDER, branchName);
        if (!BranchHeadPath.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String BranchHeadCommit = Utils.readContentsAsString(BranchHeadPath);
        return BranchHeadCommit;
    }


}



