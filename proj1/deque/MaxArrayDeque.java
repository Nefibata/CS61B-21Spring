package deque;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque<T>{
    Comparator com;
    public MaxArrayDeque(Comparator<T> c){
        this.com=c;
    }
    public T max(){
        if (size()==0)return null;
        T max=get(0);
        for (int i=0;i<size()-1;i++){
           if (com.compare(max,get(i))==-1) max = get(i);
        }
        return max;
    }
    public T max(Comparator<T> c){
        if (size()==0)return null;
        T max=get(0);
        for (int i=0;i<size()-1;i++){
            if (c.compare(max,get(i))==-1) max = get(i);
        }
        return max;
    }


}