package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    //blob存储
    public static final File blobs=join(".git\\objects","blobs");

    private byte[] content;

    private String fileName;

    public Blob (byte[] content,String fileName){
        this.content=content;
        this.fileName=fileName;
    }
    public void saveBlobStage(File newFile){ ;
        writeObject(newFile,this);
    }
    public String getId(){
        return  sha1(this);
    }
}
