package gitlet;



import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author
 */
public class Repository {
    /**
     *
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

    public static void commit(String message, Date now,List<String> parents){
        Commit nowHead=readHead();
        if (parents==null){
            parents=new ArrayList<>();
            parents.add(nowHead.getId());
        }
        if (message.equals("")){
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit newHead=new Commit(message,parents,now);
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
        Blob rmBlob;
        if (!rmFile.exists()){
            rmBlob  = new Blob(fileName);
        }else {
           rmBlob = new Blob(rmFile);
        }
        boolean stageIN = true;

        Commit nowHead=readHead();
        File [] files=stage.listFiles();
        if (files!=null){
            for (File temp:files
            ) {
                if (readObject(temp,Blob.class).getFileName().equals(rmBlob.getFileName())){
                    temp.delete();
                    stageIN=false;
                }

            }
        }
        if (nowHead.isContentNameBlob(rmBlob.getFileName())){
            File temp=join(rmStage,rmBlob.getId());
            writeObject(temp,rmBlob);
            rmFile.delete();
        }else if (stageIN){
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
            System.out.println("No such branch exists.");
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
        if (!f.equals("--")){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Commit head =readHead();
        String [] fileList=CWD.list();
        for (String st:fileList
        ) {
            if (head.isContentNameBlob(st))continue;
            if (st.equals(".gitlet"))continue;
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
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
        if (!f.equals("--")){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Commit head =readHead();
        String [] fileList=CWD.list();
        for (String st:fileList
        ) {
            if (head.isContentNameBlob(st))continue;
            if (st.equals(".gitlet"))continue;
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        File commitFile = join(Commit.commits,commitId);
        if (!commitFile.exists()) {
            String [] shortId = Commit.commits.list();
            boolean flag = true;
            for (String s:shortId
                 ) {
                if (checkShortId(s,commitId)){
                    commitFile = join(Commit.commits,s);
                    flag = false;
                    break;
                }
            }
            if (flag){
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }

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

    private static boolean checkShortId(String s1 , String shortID){
        for (int i=0;i<shortID.length();i++){
            if (!(s1.charAt(i)==shortID.charAt(i))){
                return false;
            }
        }
        return true;
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
        if (p.getParents().size()==2) System.out.println("Merge: "+p.getParents().get(0).substring(0,7) +" "+p.getParents().get(1).substring(0,7));
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
        rmBranch.delete();

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
            if (t.isFile()){
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
        writeContents(new File(readContentsAsString(Repository.head)),resetC.getId());

    }


    public static void merge(String arg) {
        if (stage.listFiles().length!=0||rmStage.listFiles().length!=0){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File targetBranch = join(branch,arg);
        if (!targetBranch.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File headBranch = new File(readContentsAsString(head));
        if (targetBranch.getName().equals(headBranch.getName())){
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Commit head =readHead();

        File f1=join(branch,arg);
        File f2 = join(Commit.commits,readContentsAsString(f1));
        Commit argBranch = readObject(f2,Commit.class);

        HashSet<String> path = new HashSet<>();
        while (!head.getMessage().equals("initial commit")){
            path.add(head.getId());
            head=readObject(join(Commit.commits,head.getParents().get(0)),Commit.class);
        }
        path.add(head.getId());
        Commit splitN = head;
        while (!argBranch.getMessage().equals("initial commit")){
            if (path.contains(argBranch.getId())){
                splitN=argBranch;
                break;
            }
            argBranch=readObject(join(Commit.commits,argBranch.getParents().get(0)),Commit.class);
        }
        
        argBranch = readObject(f2,Commit.class);
        head =readHead();
        
        
        if (argBranch.getId().equals(splitN.getId())){
            System.out.println("Given branch is an ancestor of the current branch");
            System.exit(0);
        }
        if (head.getId().equals(splitN.getId())){
            checkout(arg);
            writeContents(new File(readContentsAsString(Repository.head)),readContentsAsString(join(branch,arg)));
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        //分支父亲
        List<String> p =new LinkedList<>();
        p.add(head.getId());
        p.add(argBranch.getId());

        //把文件集合
        TreeSet<String> max = new TreeSet<>(splitN.getName_blobs().keySet());
        max.addAll(head.getName_blobs().keySet());
        max.addAll(argBranch.getName_blobs().keySet());
        boolean flag = false;

        //检查会被覆盖的文件
        for (File s:CWD.listFiles()
             ) {
            if (!s.isFile())continue;
            Blob t = new Blob(s);
            if (max.contains(s.getName())&&(!head.getBlobHashName(s.getName()).equals(t.getId()))){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        //处理sp中的文件
        for (String s:max
             ) {
            //文件在三个中都存在
            if (head.isContentNameBlob(s)&&argBranch.isContentNameBlob(s)&&splitN.isContentNameBlob(s)){
                //内容一样
                if (head.getBlobHashName(s).equals(argBranch.getBlobHashName(s))){
                    continue;
                }
                //arg新
                if (head.getBlobHashName(s).equals(splitN.getBlobHashName(s))){
                    checkout(f2.getName(),"--",s);
                    add(s);
                    continue;
                }
                //head新
                if (argBranch.getBlobHashName(s).equals(splitN.getBlobHashName(s))){
                    continue;
                }
                //文件冲突
                flag=true;
                Blob mB=mergeFile(head.getBlobHashName(s),argBranch.getBlobHashName(s),s);
                File flesh = join(CWD,s);
                writeContents(flesh, (Object) mB.getContent());
                add(s);
                continue;
            }

            //head和arg中存在
            if (head.isContentNameBlob(s)&&argBranch.isContentNameBlob(s)&&(!splitN.isContentNameBlob(s))){
                //内容一样
                if (head.getBlobHashName(s).equals(argBranch.getBlobHashName(s))){
                    continue;
                }
                //文件冲突
                flag =true;
                Blob mB=mergeFile(head.getBlobHashName(s),argBranch.getBlobHashName(s),s);
                File flesh = join(CWD,s);
                writeContents(flesh, (Object) mB.getContent());
                add(s);
                continue;

            }

            //head和sp中存在
            if(head.isContentNameBlob(s)&& splitN.isContentNameBlob(s)&&(!argBranch.isContentNameBlob(s))){
                //内容不变
                if (head.getBlobHashName(s).equals(splitN.getBlobHashName(s))){
                    rm(s);
                    continue;
                }
                //文件冲突
                flag=true;
                Blob mB=mergeFile(head.getBlobHashName(s),null,s);
                File flesh = join(CWD,s);
                writeContents(flesh, (Object) mB.getContent());
                add(s);
                continue;
            }

            //arg和sp中存在
            if(argBranch.isContentNameBlob(s)&& splitN.isContentNameBlob(s)&&(!head.isContentNameBlob(s))){
                //内容不变
                if (argBranch.getBlobHashName(s).equals(splitN.getBlobHashName(s))){
                    continue;
                }
                //文件冲突
                flag=true;
                Blob mB=mergeFile(null,argBranch.getBlobHashName(s),s);
                File flesh = join(CWD,s);
                writeContents(flesh, (Object) mB.getContent());
                add(s);
                continue;
            }

            //只在sp中存在
            if(splitN.isContentNameBlob(s)&&(!argBranch.isContentNameBlob(s))&&(!head.isContentNameBlob(s))){
                continue;
            }

            //只在head中存在
            if(head.isContentNameBlob(s)&&(!argBranch.isContentNameBlob(s))&&(!splitN.isContentNameBlob(s))){
                continue;
            }

            //只在arg中存在
            if(argBranch.isContentNameBlob(s)&&(!head.isContentNameBlob(s))&&(!splitN.isContentNameBlob(s))){
                checkout(f2.getName(),"--",s);
                add(s);
                continue;
            }

        }
        if (flag) System.out.println("Encountered a merge conflict.");
        commit("Merged "+arg+" into "+new File (readContentsAsString(Repository.head)).getName()+".",new Date(),p);

        
    }

    private static Blob mergeFile(String s1,String s2,String fileName){
        File f1,f2;
        Blob b1,b2;
        if (s1!=null){f1=join(Blob.blobs,s1);
         b1 = readObject(f1,Blob.class);}else {
            b1=new Blob(fileName,"");
        }

        if (s2!=null){f2=join(Blob.blobs,s2);
        b2 = readObject(f2,Blob.class);}else {
            b2=new Blob(fileName,"");
        }

        String merS="<<<<<<< HEAD\r\n" + new String(b1.getContent()) + "=======\r\n" +new String(b2.getContent()) +">>>>>>>\r\n";

        return new Blob(fileName,merS);

    }
}
