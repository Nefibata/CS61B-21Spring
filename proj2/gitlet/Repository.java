package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    //add的暂存区
    public static final File stage = join(GITLET_DIR,"stage");

    //rm的暂存区
    public static final File rmStage = join(GITLET_DIR,"rmStage");

    //对象存储
    public static final File obj=join(GITLET_DIR,"objects");

    //分支存储
    public static final File branch=join(GITLET_DIR,"branch");

    //head指向文件,储存的是headCommit的id;
    public static final File head=join(GITLET_DIR,"HEAD");

    /* TODO: fill in the rest of this class. */


    private static void initCommit(String message, Date now, List<String> parents){
        Commit init = new Commit(message,parents,now);
        init.saveCommit();
        try {
            head.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File branchMaster = join(branch,"master");
        writeContents(branchMaster,init.getId());
        writeContents(head,branchMaster.getPath());
    }
    //读取head的commit
    private static Commit readHead(){
        String headString = readContentsAsString(head);
        File headCommit=new File(headString);
        File headObj=join(Commit.commits,readContentsAsString(headCommit));
        return readObject(headObj,Commit.class);
    }
    private static void writeHeadBranch(String s){
        String headString = readContentsAsString(head);
        File hedCommit = new File(headString);
        writeContents(hedCommit,s);
    }
    private static void writeHead(String s){
        writeContents(head,s);
    }
    private static void clearStageAndRm(){
        for (File t:stage.listFiles()
        ) {
            t.delete();
        }
        for (File t:rmStage.listFiles()
             ) {
            t.delete();
        }

    }


    public static void init(){
        GITLET_DIR.mkdir();
        stage.mkdir();
        rmStage.mkdir();
        obj.mkdir();
        Commit.commits.mkdir();
        Blob.blobs.mkdir();
        branch.mkdir();
        initCommit("initial commit",new Date(0),new ArrayList<>());
    }

    public static void add(String fileName){
        //读取file转化成blob并检测存在file
        File temp = join(CWD,fileName);
        if (!temp.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob tempBlob = new Blob(temp);
        String BlobId=tempBlob.getId();

        //读取head的commit
        Commit tempHead = readHead();

        //检测commit里有没有blob
        boolean isContent=tempHead.isContentBlob(BlobId);
        //如果没有存入stage
        if (!isContent){
            File newFile = join(stage,BlobId);
            tempBlob.saveBlobStage(newFile);
        }
        for (File f:rmStage.listFiles()
             ) {
            if (readObject(f,Blob.class).getFileName().equals(fileName)){
                f.delete();
            }
        }
    }

    public static void commit(String message, Date now){
        Commit nowHead=readHead();
        List<String> p=new ArrayList<>();
        p.add(nowHead.getId());
        Commit newHead=new Commit(message,p,now);
        //设置当前tree
        newHead.setBlobsT(nowHead.getBlobsT(),nowHead.getName_blobs());
        File[] stageFilesList = stage.listFiles();
        File [] rmFileList =rmStage.listFiles();
        if (stageFilesList.length==0&&rmFileList.length==0){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for (File f:stageFilesList
             ) {
            Blob tempB =readObject(f,Blob.class);
            newHead.addBlob(f.getName(),tempB.getFileName());
            File temp=join(Blob.blobs,f.getName());
            writeContents(temp, (Object) readContents(f));
        }
        for (File f:rmFileList
             ) {
            Blob tempB =readObject(f,Blob.class);
            newHead.rmBlobs(tempB.getFileName());
        }

        newHead.saveCommit();
        writeHeadBranch(newHead.getId());
        clearStageAndRm();
    }

    public static void rm(String fileName){
        File rmFile = join(CWD,fileName);
        if (!rmFile.exists()){
            System.out.println("File does not exist.");
        }
        Blob rmBlob = new Blob(rmFile);
        Commit nowHead=readHead();
        File [] files=stage.listFiles();
        if (files!=null){
            for (File temp:files
            ) {
                if (readObject(temp,Blob.class).getFileName().equals(rmBlob.getFileName())){
                    temp.delete();
                }

            }
        }
        if (nowHead.isContentNameBlob(rmBlob.getFileName())){
            File temp=join(rmStage,rmBlob.getId());
            writeObject(temp,rmBlob);
        }else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        rmFile.delete();


    }

    public static void checkout(String branch_name){
        File readNowBranch=new File(readContentsAsString(head));
        if (branch_name.equals(readNowBranch.getName())){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File branchFile = join(branch,branch_name);
        if (!branchFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File branchCommit = join(Commit.commits,readContentsAsString(branchFile));
        Commit branch = readObject(branchCommit,Commit.class);
        Commit head = readHead();
        String [] fileList=CWD.list();
        for (String st:fileList
             ) {
            if (head.isContentNameBlob(st))continue;
            if (st.equals(".gitlet"))continue;
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        for (File tf:CWD.listFiles()
             ) {
            if (tf.getName().equals(".gitlet"))continue;
            else {
                tf.delete();
            }
        }
        for (String st: branch.getBlobsT()
             ) {
            File ft=join(Blob.blobs,st);
            Blob bt=readObject(ft,Blob.class);
            File wf=join(CWD,bt.getFileName());
            try {
                wf.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeContents(wf, (Object) bt.getContent());
        }
        clearStageAndRm();

        writeHead(branchFile.getPath());

    }
    public static void checkout(String f ,String file_name){
        Commit head =readHead();
        if (head.isContentNameBlob(file_name)){
           String hashName= head.getBlobHashName(file_name);
           File temp =join(Blob.blobs,hashName);
           Blob tempBlob = readObject(temp,Blob.class);
            File w =join(CWD,file_name);
            if (!w.exists()) {
                try {
                    w.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writeContents(w, (Object) tempBlob.getContent());
        }else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }
    public static void checkout(String commitId,String f ,String file_name){
        File commitFile = join(Commit.commits,commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit branch = readObject(commitFile,Commit.class);
        if (branch.isContentNameBlob(file_name)){
            String hashName= branch.getBlobHashName(file_name);
            File temp =join(Blob.blobs,hashName);
            Blob tempBlob = readObject(temp,Blob.class);
            File w =join(CWD,file_name);
            if (!w.exists()) {
                try {
                    w.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writeContents(w, (Object) tempBlob.getContent());
        }else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

    }

    public static void log(){
        Commit p=readHead();
        while (p.getParents().size()!=0){
            format_log_print(p);
            File temp=join(Commit.commits,p.getParents().get(0));
            p=readObject(temp,Commit.class);
        }
        format_log_print(p);
    }

    public static void global_log(){
        File global = Commit.commits;
        String[] arr = global.list();
        for (String t:arr
             ) {
            File temp =join(global,t);
            Commit tempC=readObject(temp,Commit.class);
            format_log_print(tempC);
        }
    }

    private static void format_log_print(Commit p) {
        System.out.println("===");
        System.out.println("commit "+p.getId());
        if (p.getParents().size()==2) System.out.println("Merge: "+p.getParents().get(0).substring(7) +" "+p.getParents().get(1).substring(7));
        System.out.println("Date: "+p.getDate());
        System.out.println(p.getMessage());
        System.out.println();
    }


    public static void find(String arg) {
        File global = Commit.commits;
        String[] arr = global.list();
        boolean flag =true;
        for (String t:arr
        ) {
            File temp =join(global,t);
            Commit tempC=readObject(temp,Commit.class);
            if (tempC.getMessage().equals(arg)){
                System.out.println(tempC.getId());
                flag=false;
            }
        }
        if (flag){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        //branch name
        File useGetName = new File(readContentsAsString(head));
        String nowHeadBranch = useGetName.getName();
        String [] branchNameArr = branch.list();
        Arrays.sort(branchNameArr);
        for (int i =0 ; i<branchNameArr.length;i++){
            if (branchNameArr[i].equals(nowHeadBranch)){
                branchNameArr[i]="*"+nowHeadBranch;
            }
        }
        format_status_print("Branches",branchNameArr);

        //stage name
        File [] stageNameArr = stage.listFiles();
        String [] stageArr = new String[stageNameArr.length];
        if (stageArr.length!=0) {
            int i = 0;
            for (File f : stageNameArr
            ) {

                stageArr[i] =readObject(f,Blob.class).getFileName();
                i++;
            }
            if (stageArr != null) Arrays.sort(stageArr);
        }
        format_status_print("Staged Files",stageArr);

        //rm name
        File [] rmNameArr = rmStage.listFiles();
        String [] rmArr = new String[rmNameArr.length];
        if (rmArr.length!=0) {
            int i = 0;
            for (File f : rmNameArr
            ) {
                rmArr[i] = readObject(f,Blob.class).getFileName();
                i++;
            }
            Arrays.sort(rmArr);
        }
        format_status_print("Removed Files",rmArr);

        //额外学分
        //Modifications Not Staged For Commit
        format_status_print("Modifications Not Staged For Commit",new String[0]);

        //Untracked Files
        format_status_print("Untracked Files",new String[0]);



    }
    private static void format_status_print(String s , String [] arr){
        System.out.println("=== "+s+" ===");
        for (String t:arr
             ) {
            System.out.println(t);
        }
        System.out.println();
    }

    public static void branch(String s) {
        File newBranch =join(branch,s);
        if (newBranch.exists()) System.out.println("A branch with that name already exists.");
        else {
            try {
                newBranch.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String nowId = readHead().getId();
        writeContents(newBranch,nowId);
    }

    public static void rm_branch(String arg) {
        File rmBranch = join(branch,arg);
        if (!rmBranch.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File useGetName = new File(readContentsAsString(head));
        String nowHeadBranch = useGetName.getName();
        if(nowHeadBranch.equals(arg)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

    }

    public static void reset(String arg) {
        File resetFile = join(Commit.commits,arg);
        if (!resetFile.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit resetC=readObject(resetFile,Commit.class);
        Commit head = readHead();
        for (File t:CWD.listFiles()
             ) {
            if (t.isFile()){
                if ((!head.isContentNameBlob(t.getName()))&&resetC.isContentNameBlob(t.getName())){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        for (File t:CWD.listFiles()
        ) {
            if (t.isFile()&& head.isContentNameBlob(t.getName())){
                t.delete();
            }
        }
        for (String s:resetC.getBlobsT()
             ) {
            File t = join(Blob.blobs,s);
            Blob wB=readObject(t,Blob.class);
            File w = join(CWD,wB.getFileName());
            try {
                w.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeContents(w,wB.getContent());
        }
        clearStageAndRm();

    }



}
