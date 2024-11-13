package deque;

import java.util.Comparator;

public class MaxArrayDeque <T> extends ArrayDeque<T>{

    public MaxArrayDeque(Comparator<T> c){

    }
    public T max(){
        return null;


    }
    public T max(Comparator<T> c){
        return null;
    }

    @Override
    public void addFirst(T item) {
        super.addFirst(item);
    }

    @Override
    public void addLast(T item) {
        super.addLast(item);
    }

    @Override
    public T removeFirst() {
        return super.removeFirst();
    }

    @Override
    public T removeLast() {
        return super.removeLast();
    }

}
