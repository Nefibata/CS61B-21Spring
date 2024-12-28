package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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
    }

    public static void commit(String message, Date now){
        Commit nowHead=readHead();
        List<String> p=new ArrayList<>();
        p.add(nowHead.getId());
        Commit newHead=new Commit(message,p,now);
        //设置当前tree
        newHead.setBlobsT(nowHead.getBlobsT(),nowHead.getName_blobs());
        File[] stageFilesList = stage.listFiles();
        if (stageFilesList.length==0){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for (File f:stageFilesList
             ) {
            Blob tempB =readObject(f,Blob.class);
            newHead.addBlob(f.getName(),tempB.getFileName());
            File temp=join(Blob.blobs,f.getName());
            writeContents(temp,readContents(f));
            f.delete();
        }

        newHead.saveCommit();
        writeHeadBranch(newHead.getId());

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
                if (temp.getName().equals(rmBlob.getId())){
                    temp.delete();
                    return;
                }

            }
        }
        if (nowHead.isContentBlob(rmBlob.getId())){
            File temp=join(rmStage,rmBlob.getId());
            writeObject(temp,rmBlob);
        }else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }


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
    public static void checkout(String branchName,String f ,String file_name){
        File branchFile = join(branch,branchName);
        if (!branchFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File branchCommit = join(Commit.commits,readContentsAsString(branchFile));
        Commit branch = readObject(branchCommit,Commit.class);
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

    private static void format_log_print(Commit p) {
        System.out.println("===");
        System.out.println("commit "+p.getId());
        if (p.getParents().size()==2) System.out.println("Merge: "+p.getParents().get(0).substring(7) +" "+p.getParents().get(1).substring(7));
        System.out.println("Date: "+p.getDate());
        System.out.println(p.getMessage());
    }

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
        File headCommit=join(Commit.commits,headString);
        return readObject(headCommit,Commit.class);
    }
    private static void writeHeadBranch(String s){
        String headString = readContentsAsString(head);
        File hedCommit = new File(headString);
        writeContents(hedCommit,s);
    }
    private static void writeHead(String s){
        writeContents(head,s);
    }

}
