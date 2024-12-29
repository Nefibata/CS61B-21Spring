package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */

    //commit存储
    public static final File commits=join(Repository.obj,"commits");

    private String message;

    private Date now;

    private List<String> parents=new ArrayList<>();

    //连接Blob
    private TreeSet<String> blobs=new TreeSet<>();

    //连接名字和Blob
    private TreeMap<String,String> name_blobs=new TreeMap<>();

    /* TODO: fill in the rest of this class. */
    public Commit(String message,List<String> parents,Date now){
        this.message=message;
        this.parents=parents;
        this.now=now;
    }
    public TreeSet<String> getBlobsT(){
        return this.blobs;
    }
    public void setBlobsT(TreeSet<String> blobs,TreeMap<String,String> name_blobs){
        this.blobs=blobs;
        this.name_blobs=name_blobs;
    }
    public void addBlob(String blobId,String blobName){
        this.blobs.add(blobId);
        this.name_blobs.put(blobName,blobId);
    }
    public boolean isContentBlob(String blobId){
        return this.blobs.contains(blobId);
    }


    public void saveCommit(){
        File temp = join(commits,getId());
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(temp,this);
    }
    public String getId(){
        return sha1(message,now.toString(),parents.toString(),blobs.toString(),name_blobs.toString());
    }
    public List<String> getParents(){
        return parents;
    }
    public String getDate(){
      /*  Formatter formatter = new Formatter(Locale.US);
        formatter.format("EEE MMM d HH:mm:ss yyyy Z",now);
        return formatter.toString();

       */
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(now);
    }
    public String getMessage(){
        return message;
    }
    public TreeMap<String,String> getName_blobs(){
        return this.name_blobs;
    }

    public boolean isContentNameBlob(String s){
        return name_blobs.containsKey(s);
    }
    public String getBlobHashName(String s){
        return name_blobs.get(s);
    }

    public void rmBlobs(String name){
       String s= this.name_blobs.remove(name);
       this.blobs.remove(s);
    }
}
