package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public static final File stage = new File(GITLET_DIR, "index");

    /** The trash file for file removal. */
    public static final File trash = new File(GITLET_DIR, "trash");

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
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // build the staging area.
        Map<String, String> stageTable = buildStage();
        Map<String, String> trashTable = buildTrash();
        // If the file is added it will no longer be staged for removal.
        trashTable.remove(fileName);
        Utils.writeObject(trash, new GitTree(trashTable));
        // If the current working version of the file is not added
        // and is not identical to the version in the current commit,
        // stage it and remove it from trash if it's there.
        if (!(stageTable.containsValue(newFile.getSHA1())) & !(fileTable.containsValue(newFile.getSHA1()))) {
            stageTable.put(fileName, newFile.getSHA1());
            newFile.saveBlob();
        }
        // If the current working version of the file is identical to the version in the current commit,
        // do not stage it to be added, and remove it from the staging area if it is already there
        if (stageTable.containsValue(newFile.getSHA1()) & fileTable.containsValue(newFile.getSHA1())) {
            stageTable.remove(fileName);
        }
        GitTree stageTree = new GitTree(stageTable);
        Utils.writeObject(stage, stageTree);
    }

    /**  Remove a file from the staging area and the working directory if it's tracked. */
    public static void rm(String fileName) throws IOException {
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // remove the file from the working directory if it's tracked in the current commit.
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
                Utils.writeObject(stage, new GitTree(stageTable));
            } else {
                // load the trash.
                Map<String, String> trashTable = buildTrash();
                // write file name to trash table if it's staged or tracked.
                trashTable.put(fileName, "to be deleted");
                Utils.writeObject(trash, new GitTree(trashTable));
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
        if (!stage.exists()) {
            stageTable = new TreeMap<>();
        } else {
            GitTree stageTree = Utils.readObject(stage, GitTree.class);
            stageTable = stageTree.getTable();
        }
        return stageTable;
    }

    /** load the existing trash or build a new one. */
    public static Map<String, String> buildTrash() {
        // build the staging area.
        Map<String, String> trashTable;
        if (!trash.exists()) {
            trashTable = new TreeMap<>();
        } else {
            GitTree trashTree = Utils.readObject(trash, GitTree.class);
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
        if (!stage.exists() & !trash.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // get the head commit file table.
        Map<String, String> fileTable = getHeadCommitFileTable();
        // get the stage GitTree and add them to the file table.
        if (stage.exists()) {
            GitTree stageTree = Utils.readObject(stage, GitTree.class);
            Map<String, String> stageTable = stageTree.getTable();
            fileTable.putAll(stageTable);
            // empty the stage area.
            stage.delete();
        }
        // get the trash GitTree and remove them from the file table.
        if (trash.exists()) {
            GitTree trashTree = Utils.readObject(trash, GitTree.class);
            Map<String, String> trashTable = trashTree.getTable();
            Set<String> trashKeys = trashTable.keySet();
            fileTable.keySet().removeAll(trashKeys);
            // empty the trash.
            trash.delete();
        }
        // save the new GitTree.
        GitTree newGitTree = new GitTree(fileTable);
        newGitTree.saveGitTree();
        // get the head commit.
        String headCommit = getHeadCommitSha1();
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
        checkoutCommit(getHeadCommitSha1(), commitID);
        // reset the head to the current commit.
        Utils.writeContents(getHeadPath(), commitID);
        // clean the stage and trash area.
        stage.delete();
        trash.delete();
    }

    /**  Creates a new branch with the given name,
     * and points it at the current head commit. */
    public static void branch (String branchName) throws IOException {
        // get current head commit SHA1
        String HeadCommitSha1 = getHeadCommitSha1();
        // The branch file that saves HEAD pointer in the branch dir.
        File branchFile = join(Branch_FOLDER, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branchFile.createNewFile();
        // points it at the current head commit but DON'T immediately switch to the newly created branch
        String ActiveBranch = "refs/" + branchName;
        File headPath = new File(GITLET_DIR, ActiveBranch);
        Utils.writeContents(headPath, HeadCommitSha1);
    }

    /** Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch,
     * or anything like that. */
    public static void rm_branch (String branchName) throws IOException {
        // The branch file that saves HEAD pointer in the branch dir.
        File branchFile = join(Branch_FOLDER, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // get the current active branch
        String ActiveBranch_path = Utils.readContentsAsString(ActiveBranchFile);
        String ActiveBranch = ActiveBranch_path.substring(5,ActiveBranch_path.length());
        // quit if the given branch is the one you’re currently working on.
        if (branchName.equals(ActiveBranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else {
            branchFile.delete();
        }

    }

    /** Takes all files in the given commit,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * Any files that are tracked in the current commit but are not present in the checked-out commit are deleted. */
    public static void checkoutCommit (String commitID_C,String commitID_B) throws IOException {
        // get the commit of the current branch.
        Commit C = Commit.readCommit(commitID_C);
        Map<String, String> fileTable = C.getTable();
        Set<String> CKeySet = fileTable.keySet();

        // get the commit of the given commit.
        Commit B = Commit.readCommit(commitID_B);
        Map<String, String> fileTableBranch = B.getTable();
        Set<String> BKeySet = fileTableBranch.keySet();

        List<String> FileNames = Utils.plainFilenamesIn(CWD);
        Set<String> CWDSet = new HashSet<>(FileNames);
        // get the intersection of the CWD and Bkey sets.
        Set<String> inter_B_CWD = new HashSet<>(CWDSet);
        inter_B_CWD.retainAll(BKeySet);
        // exclude files in Ckey set.
        inter_B_CWD.removeAll(CKeySet);
        if (!inter_B_CWD.isEmpty()) {
            // quit if a working file is untracked in the current branch and would be overwritten by the checkout.
            // check this before doing anything else.
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        // iterate over the files in the given commit.
        for (String BKey : BKeySet) {
            File file_tmp = join(CWD, BKey);
            //  overwrite the versions of the files that are tracked or create it if not exist.
                // create/replace the file in working dir.
                blob fileBlob = blob.readBlob(fileTableBranch.get(BKey));
                fileBlob.WriteBlobToFile();
        }

        // delete the files that are tracked in the current branch and are not present in the given branch
        CKeySet.removeAll(BKeySet);
        for (String CKey : CKeySet) {
            File file_tmp = join(CWD, CKey);
            file_tmp.delete();
        }

    }


    /** Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions of the files that are already there if they exist.
     * Also, at the end of this command, the given branch will now be considered the current branch (HEAD).
     * Any files that are tracked in the current branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the current branch */
    public static void checkoutBranch (String branchName) throws IOException {

        // get the current active branch name
        String ActiveBranch_path = Utils.readContentsAsString(ActiveBranchFile);
        String ActiveBranch = ActiveBranch_path.substring(5,ActiveBranch_path.length());
        if (branchName.equals(ActiveBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkoutCommit(getHeadCommitSha1(), getBranchHeadCommitSha1(branchName));

        // reset the active branch, clear the stage area and trash.
        Utils.writeContents(ActiveBranchFile, "refs/" + branchName);
        stage.delete();
        trash.delete();
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
        // get the commit of the current branch.
        Commit C = Commit.readCommit(getHeadCommitSha1());
        Map<String, String> fileTable = C.getTable();
        Set<String> CKeySet = fileTable.keySet();

        // get the commit of the given commit.
        Commit B = Commit.readCommit(commitID);
        Map<String, String> fileTable_B = B.getTable();
        Set<String> BKeySet = fileTable_B.keySet();

        // check if the file exists in the cwd.
        File file_tmp = join(CWD, fileName);

        // check if the file exists in the commit gitTree.
        if (!fileTable_B.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        // Quit if a working file is untracked in the current branch
        // and would be overwritten by the checkout.
        if (!fileTable.containsKey(fileName) & file_tmp.exists()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        // replace the file in working dir.
        blob fileBlob = blob.readBlob(fileTable_B.get(fileName));
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

    /** displays information about all commits ever made regardless of the order. */
    public static void global_log() throws IOException {
        // get all commits in the Commits_FOLDER
        List<String> CommitNames = Utils.plainFilenamesIn(Commits_FOLDER);
        for (String CommitName : CommitNames) {
            Commit tmp = Commit.readCommit(CommitName);
            System.out.println(tmp.toString());
        }
    }

    /** Prints out the ids of all commits that have the given commit message, one per line. */
    public static void find(String message) throws IOException {
        // get all commits in the Commits_FOLDER
        List<String> CommitNames = Utils.plainFilenamesIn(Commits_FOLDER);
        int n = 0;
        for (String CommitName : CommitNames) {
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
        List<String> BranchNames = Utils.plainFilenamesIn(Branch_FOLDER);
        // get current branch
        String ActiveBranch_path = Utils.readContentsAsString(ActiveBranchFile);
        String ActiveBranch = ActiveBranch_path.substring(5,ActiveBranch_path.length());
        System.out.println("=== Branches ===");
        // for each branch print the branch name and add * if it's the active branch.
        for (String BranchName : BranchNames) {
            if (BranchName.equals(ActiveBranch)) {
                System.out.println(String.format("*%s", BranchName));
            } else {
                System.out.println(BranchName);
            }
        }
        // display what files have been staged
        System.out.println("\n=== Staged Files ===");
        // get the stage GitTree and print all the keys.
        if (stage.exists()) {
            GitTree stageTree = Utils.readObject(stage, GitTree.class);
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
        if (trash.exists()) {
            GitTree trashTree = Utils.readObject(trash, GitTree.class);
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
        if (stage.exists() || trash.exists()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        // get the current active branch name
        String ActiveBranch_path = Utils.readContentsAsString(ActiveBranchFile);
        String ActiveBranch = ActiveBranch_path.substring(5,ActiveBranch_path.length());
        // can't merge a branch with itself.
        if (branchName.equals(ActiveBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        // No such branch exists.
        File BranchHeadPath = join(Branch_FOLDER, branchName);
        if (!BranchHeadPath.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // get the commit of the current branch.
        Commit C = Commit.readCommit(getHeadCommitSha1());
        Map<String, String> fileTable_C = C.getTable();
        Set<String> CKeySet = fileTable_C.keySet();
        // get the ancestors of the current branch.
        List<String> C_Ancestor = getCommitAncestor(C);
        // get the commit of the given branch.
        Commit B = Commit.readCommit(getBranchHeadCommitSha1(branchName));
        Map<String, String> fileTable_B = B.getTable();
        Set<String> BKeySet = fileTable_B.keySet();
        // get the ancestors of the given branch.
        List<String> B_Ancestor = getCommitAncestor(B);

        // get the split point
        int splitIndex = 0;
        for (int i = 1; i <= Math.min(C_Ancestor.size(), B_Ancestor.size()); i ++) {
            if (!C_Ancestor.get(C_Ancestor.size()-i).equals(B_Ancestor.get(B_Ancestor.size()-i))) {
                break;
            }
            splitIndex ++;
        }
        if (splitIndex == B_Ancestor.size()) {
            // If the split point is the same commit as the give branch,
            // then we do nothing
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitIndex == C_Ancestor.size()) {
            //If the split point is the current branch,
            // then check out the given branch
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        // get the commit at the split point
        Commit S = Commit.readCommit(C_Ancestor.get(C_Ancestor.size() - splitIndex));
        Map<String, String> fileTable_S = S.getTable();
        Set<String> SKeySet = fileTable_S.keySet();
        // get the union of the three key sets
        Set<String> uni_3 = new HashSet<String>();
        uni_3.addAll(SKeySet);
        uni_3.addAll(CKeySet);
        uni_3.addAll(BKeySet);
        // get the intersection of the three key sets.
        Set<String> inter_3 = new HashSet<>(SKeySet);
        inter_3.retainAll(CKeySet);
        inter_3.retainAll(BKeySet);
        // get the intersection of S and C.
        Set<String> inter_SC = new HashSet<>(SKeySet);
        inter_SC.retainAll(CKeySet);
        // get the intersection of S and B.
        Set<String> inter_SB = new HashSet<>(SKeySet);
        inter_SB.retainAll(BKeySet);
        // get the intersection of S and C.
        Set<String> inter_CB = new HashSet<>(CKeySet);
        inter_CB.retainAll(BKeySet);

        // check if an untracked file in the current commit would be overwritten by the merge.
        // before doing anything else.
        List<String> FileNames = Utils.plainFilenamesIn(CWD);
        Set<String> CWDSet = new HashSet<>(FileNames);
        // get the intersection of the CWD and Bkey sets.
        Set<String> inter_B_CWD = new HashSet<>(CWDSet);
        inter_B_CWD.retainAll(BKeySet);
        // exclude files in Ckey set.
        inter_B_CWD.removeAll(CKeySet);
        if (!inter_B_CWD.isEmpty()) {
            for (String file : inter_B_CWD) {
                if (!SKeySet.contains(file) || (SKeySet.contains(file) & !fileTable_S.get(file).equals(fileTable_B.get(file)))) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }


        // iterate over files
        for (String file : uni_3) {
            // if file is tracked in all the three sets.
            if (inter_3.contains(file)) {

                // 1) S = C != B : check B and stage
                if (fileTable_S.get(file).equals(fileTable_C.get(file)) & !fileTable_S.get(file).equals(fileTable_B.get(file))) {
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
                if (!fileTable_S.get(file).equals(fileTable_C.get(file))
                        & !fileTable_S.get(file).equals(fileTable_B.get(file))
                        & !fileTable_C.get(file).equals(fileTable_B.get(file))) {
                     // Any files modified in different ways in the current and given branches are in conflict.
                    // replace the contents of the conflicted file and stage the result.
                    mergeHelper(fileTable_C.get(file),fileTable_B.get(file));
                    add(file);
                }

            } else if (inter_SC.contains(file)) {
                if (fileTable_S.get(file).equals(fileTable_C.get(file))) {
                    // 5) S = C !B : remove and untrack
                    // Any files present at the split point,
                    // unmodified in the current branch,
                    // and absent in the given branch should be removed (and untracked).
                    rm(file);
                } else {
                    // 6) S != C !B : conflict
                    mergeHelper(fileTable_C.get(file));
                }

            } else if (inter_SB.contains(file)) {
                // 7) S = B !C : remain
                // 8) S != B !C : conflict
                if (!fileTable_S.get(file).equals(fileTable_B.get(file))) {
                    mergeHelper(fileTable_C.get(file));
                }

            } else if (inter_CB.contains(file)) {
                // 9)  !S C = B : remain
                // 10) !S C != B : conflict
                if (!fileTable_C.get(file).equals(fileTable_B.get(file))) {
                    // If the file was absent at the split point
                    // and has different contents in the given and current branches,
                    // replace the contents of the conflicted file and stage the result.
                    mergeHelper(fileTable_C.get(file),fileTable_B.get(file));
                }

                // 11) !S C !B : remain
                // 12) !S !C B : check and stage
            } else if (BKeySet.contains(file)) {
                checkoutFile(B.getSHA1(), file);
                add(file);
                // 13) S !C !B : remain
            }
        }
        String msg = "Merged " + branchName + " into " + ActiveBranch + ".";
        commit(msg,getBranchHeadCommitSha1(branchName));
    }

    /** merge one txt files with an empty file and save to the working directory */
    public static void mergeHelper(String fileName1) throws IOException {
        mergeHelper(fileName1, null);
    }

    /** merge two txt files with the same name and different gitSHA1 and save to the working directory */
    public static void mergeHelper(String fileName1, String fileName2) throws IOException {
        blob fileBlob_C = blob.readBlob(fileName1);
        String file_C = fileBlob_C.FileContent;
        String tmp;
        if (fileName2 != null) {
            blob fileBlob_B = blob.readBlob(fileName2);
            String file_B = fileBlob_B.FileContent;
            if (!fileBlob_C.FileName.equals(fileBlob_B.FileName)) {
                System.out.println("Two files must have the same file names.");
                System.exit(0);
            }
            tmp = "<<<<<<< HEAD\n" + file_C + "=======\n" + file_B + ">>>>>>>";
        } else {
            tmp = "<<<<<<< HEAD\n" + file_C + "=======\n" + ">>>>>>>";
        }
        File newFile = new File(CWD, fileBlob_C.FileName);
        newFile.createNewFile();
        Utils.writeContents(newFile, tmp);
        System.out.println("Encountered a merge conflict.");
    }

    public static List<String> getCommitAncestor(Commit C) {
        // get the commit of the split point
        List<String> C_Ancestor = new ArrayList<String>();
        C_Ancestor.add(C.getSHA1());
        while (C.getParent() != null) {
            C = Commit.readCommit(C.getParent());
            C_Ancestor.add(C.getSHA1());
        }
        return C_Ancestor;
    }

    public static Map<String, String> getHeadCommitFileTable() {
        Commit C = Commit.readCommit(getHeadCommitSha1());
        return  C.getTable();
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

    public static String getBranchHeadCommitSha1(String branchName) {
        File BranchHeadPath = join(Branch_FOLDER, branchName);
        if (!BranchHeadPath.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String BranchHeadCommit = Utils.readContentsAsString(BranchHeadPath);
        return BranchHeadCommit;
    }


}



