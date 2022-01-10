package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @quanjingchen@gmail.com
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must have at least one argument");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                try {
                    Repository.init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "add":
                try {
                    Repository.add(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "rm":
                try {
                    Repository.rm(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "branch":
                try {
                    Repository.branch(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "rm-branch":
                try {
                    Repository.rm_branch(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "checkout":
                if (args.length == 2) {
                    // Takes all files in the commit at the head of the given branch,
                    // and puts them in the working directory, overwriting the versions of
                    // the files that are already there if they exist.
                    try {
                        Repository.checkoutBranch(args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String secondArg = args[1];
                    if (secondArg.equals("--")) {
                        // Takes the version of the file as it exists in the head commit and puts it in the working directory,
                        // overwriting the version of the file that’s already there if there is one.
                        // The new version of the file is not staged
                        try {
                            Repository.checkoutHead(args[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Takes the version of the file as it exists in the commit with the given id,
                        // and puts it in the working directory, overwriting the version of the file
                        // that’s already there if there is one.
                        try {
                            Repository.checkoutFile(args[1], args[3]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "reset":
                try {
                    Repository.reset(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "log":
                try {
                    Repository.log();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "global-log":
                try {
                    Repository.global_log();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "status":
                try {
                    Repository.status();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "merge":
                try {
                    Repository.merge(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "find":
                try {
                    Repository.find(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
