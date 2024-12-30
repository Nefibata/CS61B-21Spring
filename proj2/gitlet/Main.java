package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
import gitlet.Utils;

import java.util.Date;

public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        String firstArg = args[0];
        if (args.length==0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        switch(firstArg) {
            case "init":
                checkInitBefore();
                checkOperands(1,args);
                Repository.init();
                break;
            case "add":
                checkInit();
                checkOperands(2,args);
                Repository.add(args[1]);
                break;
            case "commit":
                checkInit();
                if(args.length==1){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                checkOperands(2,args);
                Repository.commit(args[1],new Date(),null);
                break;

            case "rm":
                checkInit();
                checkOperands(2,args);
                Repository.rm(args[1]);
                break;

            case "checkout":
                checkInit();
                if(args.length==2){
                    Repository.checkout(args[1]);
                    break;
                }
                if (args.length==3){
                    Repository.checkout(args[1],args[2]);
                    break;
                }
                if (args.length==4){
                    Repository.checkout(args[1],args[2],args[3]);
                    break;
                }
                System.out.println("Incorrect operands.");
                System.exit(0);
                break;

            case "log":
                checkInit();
                checkOperands(1,args);
                Repository.log();
                break;

            case "global-log":
                checkInit();
                checkOperands(1,args);
                Repository.global_log();
                break;

            case "find" :
                checkInit();
                checkOperands(2,args);
                Repository.find(args[1]);
                break;

            case "status":
                checkInit();
                checkOperands(1,args);
                Repository.status();
                break;

            case "branch":
                checkInit();
                checkOperands(2,args);
                Repository.branch(args[1]);
                break;

            case "rm-branch":
                checkInit();
                checkOperands(2,args);
                Repository.rm_branch(args[1]);
                break;

            case "reset":
                checkInit();
                checkOperands(2,args);
                Repository.reset(args[1]);
                break;

            case "merge":
                checkInit();
                checkOperands(2,args);
                Repository.merge(args[1]);
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
                break;
        }
    }
    public static void checkInit(){
        if (!Repository.GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    public static void checkInitBefore(){
        if (Repository.GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }
    public static void checkOperands(int nums,String[] args){
        if (args.length!=nums){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
