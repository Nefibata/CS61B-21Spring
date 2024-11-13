package deque;




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
        head++;
        head=head%arLength;
        arLength+=arLength;
        Object [] AR=new Object[arLength];
        for (int i=head,j=1;j<=size;i++,j++){
            i=i%this.ar.length;
            AR[j]=this.ar[i];
        }
        this.ar=AR;
        head=0;
        tail=size+1;


    }
    private void divArr(){
        head++;
        head=head%arLength;
        arLength=arLength/2;
        Object [] AR=new Object[arLength];
        for (int i=head,j=1;j<=size;i++,j++){
            i=i%this.ar.length;
            AR[j]=this.ar[i];
        }
        this.ar=AR;
        head=0;
        tail=size+1;

    }

    @Override
    public void addFirst(T item) {
        if (tail==head) doubleArr();
        this.ar[head]=item;
        head--;
        head+=arLength;
        head=head%arLength;
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
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        StringBuilder sb=new StringBuilder();
        head++;
        head=head%arLength;
        for (int i=head,j=1;j<=size;i++,j++){
            i=i%arLength;
            sb.append(ar[i].toString());
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        System.out.println(sb);

    }

    @Override
    public T removeFirst() {
        if (isEmpty())return null;
        if (size<=arLength/4&&arLength!=8) divArr();
        Object temp;
        head++;
        head=head%arLength;
        temp = this.ar[head];
        size--;
        return (T) temp;
    }

    @Override
    public T removeLast() {
        if (isEmpty())return null;
        if (size<=arLength/4&&arLength!=8) divArr();
        tail--;
        tail+=arLength;
        tail=tail%arLength;
        T temp= (T) this.ar[tail];
        size--;
        return temp;
    }

    @Override
    public T get(int index) {
        int temp=head;
        temp+=index;
        temp++;
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
            head++;
            head=head%arLength;
            T temp = (T) this.ar[head];
            size--;
            return temp;
        }
    }
    @Override
    public boolean equals(Object o){
        if (o instanceof Deque ) {
            Deque<T> temp = (Deque) o;
            if (temp.size() != this.size) return false;
            for (int i = 0; i < this.size(); i++) {
                T o1 = this.get(i);
                T o2 = temp.get(i);
                if (!o1.equals(o2)) return false;
            }
            return true;
        }
        return false;

    }

}
