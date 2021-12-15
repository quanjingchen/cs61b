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
            case "commit":
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else {
                    String secondArg = args[1];
                    if (secondArg.equals("--")) {
                        try {
                            Repository.checkoutHead(args[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Repository.checkoutFile(args[1], args[3]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "log":
                try {
                    Repository.log();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;


        }
    }
}
