package deque;

public class ListNode<T> {
    private T value;
    private ListNode<T> pr;
    private ListNode<T> next;
    public ListNode(T value,ListNode<T> pr,ListNode<T> next){
        this.next=next;
        this.pr=pr;
        this.value=value;
    }

    public ListNode<T> getNext() {
        return next;
    }

    public T getValue() {
        return value;
    }

    public ListNode<T> getPr() {
        return pr;
    }

    public void setNext(ListNode<T> next) {
        this.next = next;
    }

    public void setPr(ListNode<T> pr) {
        this.pr = pr;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
