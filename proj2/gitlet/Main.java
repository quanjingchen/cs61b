package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  Wrote by Quanjing Chen @quanjingchen@gmail.com
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                incorrectError(args.length, 0);
                try {
                    Repository.init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "add":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.add(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "rm":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.rm(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "commit":
                initError();
                incorrectError(args.length, 1);
                if (args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "branch":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.branch(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "rm-branch":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.rmBranch(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "checkout":
                initError();
                if (args.length > 4 || args.length < 2) {
                    System.out.println("Incorrect operands.");
                }
                if (args.length == 2) {
                    // Takes all files in the commit at the head of the given branch,
                    // and puts them in the working directory, overwriting the versions of
                    // the files that are already there if they exist.
                    try {
                        Repository.checkoutBranch(args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (args.length == 3) {
                        // Takes the version of the file as it exists in the head commit and puts it in the working directory,
                        // overwriting the version of the file that’s already there if there is one.
                        // The new version of the file is not staged
                    if (args[1].equals("--")) {
                        try {
                            Repository.checkoutHead(args[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                } else if (args.length == 4) {
                    // Takes the version of the file as it exists in the commit with the given id,
                    // and puts it in the working directory,
                    // overwriting the version of the file that’s already there if there is one.
                    // The new version of the file is not staged.
                    if (args[2].equals("--")) {

                        try {
                            Repository.checkoutFile(args[1], args[3]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                }
                break;
            case "reset":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.reset(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "log":
                initError();
                incorrectError(args.length, 0);
                try {
                    Repository.log();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "global-log":
                initError();
                incorrectError(args.length, 0);
                try {
                    Repository.globalLog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "status":
                initError();
                incorrectError(args.length, 0);
                try {
                    Repository.status();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "merge":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.merge(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "find":
                initError();
                incorrectError(args.length, 1);
                try {
                    Repository.find(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }

    public static void initError() {
        File CWD = new File(System.getProperty("user.dir"));
        /** The .gitlet directory. */
        File GITLET_DIR = join(CWD, ".gitlet");
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }

    public static void incorrectError(int commandLength, int number) {
        if (commandLength - 1 != number) {
            System.out.println("Incorrect operands.");
        }
    }
}
