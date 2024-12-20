package gitlet;

import net.sf.saxon.trans.SymbolicName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    //对象存储
    public static final File obj=join(GITLET_DIR,"objects");

    //分支存储
    public static final File branch=join(GITLET_DIR,"branch");

    //head指向文件,储存的是headComnit的id;
    public static final File head=join(GITLET_DIR,"HEAD");

    /* TODO: fill in the rest of this class. */
    public static void init(){
        GITLET_DIR.mkdir();
        stage.mkdir();
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
        Blob tempBlob = new Blob(readContents(temp),fileName);
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
        newHead.setBlobsT(nowHead.getBlobsT());
        File[] stageFilesList = stage.listFiles();
        for (File f:stageFilesList
             ) {
            newHead.addBlob(f.getName());
            File temp=join(Blob.blobs,f.getName());
            writeContents(temp,readContents(f));
            f.delete();
        }

        newHead.saveCommit();
        writeContents(head,newHead.getId());
        ////还缺少分支转换 Please enter a commit message. No changes added to the commit.

    }
    private static void initCommit(String message, Date now, List<String> parents){
        Commit init = new Commit(message,parents,now);
        init.saveCommit();
        try {
            head.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(head,init.getId());
        File branchMaster = join(branch,"master");
        writeContents(branchMaster,init.getId());
    }

    //读取head的commit
    private static Commit readHead(){
        String headString = readContentsAsString(head);
        File hedCommit = join(Commit.commits,headString);
        Commit tempHead = readObject(hedCommit,Commit.class);
        return tempHead;
    }



}
