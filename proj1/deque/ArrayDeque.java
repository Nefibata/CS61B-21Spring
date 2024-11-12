package deque;


import afu.org.checkerframework.checker.oigj.qual.O;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>,Iterable<T>{
    private Object[] ar ;
    private int arLength=8;
    private  int size;
    private int head;
    private  int tail;
    public ArrayDeque (){
        this.ar=new Object[arLength];
        this.head=0;
        this.tail=1;
        size=0;
    }
    private void doubleArr(){
        arLength+=arLength;
        Object [] AR=new Object[arLength];
        int i=0;
        for (T temp:this
             ) {
            AR[i]=temp;
            i++;
        }
        this.ar=AR;
        head=0;
        tail=i+1;


    }
    private void divArr(){
        arLength=arLength/2;
        Object [] AR=new Object[arLength];
        int i=0;
        for (T temp:this
        ) {
            AR[i]=temp;
            i++;
        }
        this.ar=AR;
        head=0;
        tail=i+1;

    }

    @Override
    public void addFirst(T item) {
        if (tail==head) doubleArr();
        head--;
        head+=arLength;
        head=head%arLength;
        this.ar[head]=item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (tail==head) doubleArr();
        this.ar[tail]=item;
        tail++;
        tail=tail%arLength;
        size++;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {

    }

    @Override
    public T removeFirst() {
        if (size<=arLength/4) divArr();
        T temp= (T) this.ar[head];
        head++;
        head=head%arLength;
        size--;
        return temp;
    }

    @Override
    public T removeLast() {
        if (size<=arLength/4) divArr();
        T temp= (T) this.ar[tail];
        tail--;
        tail+=arLength;
        tail=tail%arLength;
        size--;
        return temp;
    }

    @Override
    public T get(int index) {
        int temp=head;
        temp+=index;
        temp=temp%arLength;
        return (T)ar[temp];
    }

    @Override
    public Iterator<T> iterator() {
        return new arIterator<>(size,ar,head,arLength);
    }

    private class arIterator<T> implements Iterator<T>{
        private Object [] ar;
        private int size;
        private int head;
        private int arLength;
        arIterator(int size,Object [] ar,int head,int arLength){
            this.size=size;
            this.ar=ar;
            this.head=head;
            this.arLength=arLength;
        }

        @Override
        public boolean hasNext() {
            return size!=0;
        }

        @Override
        public T next() {
            T temp = (T) this.ar[head];
            head--;
            head+=arLength;
            head=head%arLength;
            size--;
            return temp;
        }
    }
}
