package deque;

import java.util.Iterator;

public class LinkedListDeque <T> implements Deque<T>,Iterable <T>{
    private int size;
    private  ListNode<T> head;
    private ListNode<T> tail;
    public LinkedListDeque (){
        this.head=new ListNode<T>(null,null,null);
        this.tail=new ListNode<T>(null,null,null);
        this.head.setNext(tail);
        this.tail.setPr(head);
        size=0;
    }
    public void addFirst(T item){
        ListNode<T> temp = new ListNode<>(item,head,head.getNext());
        head.getNext().setPr(temp);
        head.setNext(temp);
        size++;
    }
    public void addLast(T item){
        ListNode<T> temp = new ListNode<>(item,tail.getPr(),tail);
        tail.getPr().setNext(temp);
        tail.setPr(temp);
        size++;
    }
    public boolean isEmpty(){
        return size==0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        StringBuilder s= new StringBuilder();
        ListNode<T> temp=this.head.getNext();
        for (int i=0;i<size;i++){
            T tempT=temp.getValue();
            s.append(tempT.toString());
            s.append(" ");
        }
        if (size!=0){
            s.deleteCharAt(s.length()-1);
            System.out.println(s);
        }
    }
    public T removeFirst(){
        ListNode<T> temp=this.head.getNext();
        if (temp==tail)return null;
        this.head.setNext(temp.getNext());
        temp.getNext().setPr(head);
        temp.setNext(null);
        temp.setPr(null);
        T removeT=temp.getValue();
        size--;
        return removeT;
    }
    public T removeLast(){
        ListNode<T> temp=this.tail.getPr();
        if (temp==head)return null;
        this.tail.setPr(temp.getPr());
        temp.getPr().setNext(tail);
        temp.setNext(null);
        temp.setPr(null);
        T removeT=temp.getValue();
        size--;
        return removeT;
    }
    public T get(int index){
        if (index>size-1)return null;
        ListNode<T> temp=this.head;
        for (int i=0;i<=index;i++){
            temp=temp.getNext();
        }
        return temp.getValue();

    }
    public Iterator<T> iterator(){
        return new DequeIterator<T>(head.getNext(),size);

    }
    private  class  DequeIterator<T> implements Iterator<T>{
        private ListNode< T> head;
        private int count;
        private DequeIterator(ListNode<T> head,int count){
            this.head=head;
            this.count=count;
        }
        @Override
        public boolean hasNext() {
            return count!=0;
        }

        @Override
        public T next() {
            T temp=head.getValue();
            head=head.getNext();
            count--;
            return temp;
        }
    }
    public boolean equals(Object o){
        if (!( o instanceof LinkedListDeque)){
            return false;
        }
        LinkedListDeque<T> temp= (LinkedListDeque<T>) o;
        if (temp.size!=this.size)return false;
        for (int i=0;i<this.size();i++){
            T o1=this.get(i);
            T o2=temp.get(i);
            if (!o1.equals(o2))return false;
        }
        return true;
    }
    public T getRecursive(int index){
        return Recursive(index,head.getNext());
    }
    public T Recursive(int index,ListNode<T > head){
        if(index==0)return head.getValue();
        return Recursive(index-1,head.getNext());
    }


}
