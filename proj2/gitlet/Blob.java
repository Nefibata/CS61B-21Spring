package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    //blob存储
    public static final File blobs=join(Repository.obj,"blobs");

    private final byte[] content;

    private final String fileName;

    public Blob (File file){
        this.content=readContents(file);
        this.fileName= file.getName();
    }
    public void saveBlobStage(File newFile){ ;
        writeObject(newFile,this);
    }
    public String getId(){
        return  sha1(Arrays.toString(content),fileName);
    }
    public String getFileName(){
        return fileName;
    }
    public byte[] getContent() {
        return content;
    }

}
