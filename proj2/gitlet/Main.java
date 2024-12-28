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
        if (args.length==1){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        switch(firstArg) {
            case "init":
                checkInitBefore();
                checkOperands(2,args);
                Repository.init();
                break;
            case "add":
                checkInit();
                checkOperands(3,args);
                Repository.add(args[2]);
                break;
            case "commit":
                checkInit();
                if(args.length==2){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                checkOperands(3,args);
                Repository.commit(args[2],new Date());
                break;

            case "rm":
                checkInit();
                checkOperands(3,args);
                Repository.rm(args[2]);
                break;

            case "checkout":
                checkInit();
                if(args.length==3){
                    Repository.checkout(args[2]);
                    break;
                }
                if (args.length==4){
                    Repository.checkout(args[2],args[3]);
                    break;
                }
                if (args.length==5){
                    Repository.checkout(args[2],args[3],args[4]);
                    break;
                }
                System.out.println("Incorrect operands.");
                System.exit(0);
                break;

            case "log":
                checkInit();
                checkOperands(2,args);
                Repository.log();
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
