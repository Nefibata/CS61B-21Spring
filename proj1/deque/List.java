package deque;

public class List <T> {
    private T value;
    private List<T> pr;
    private List<T> next;
    public List(T value,List<T> pr,List<T> next){
        this.next=next;
        this.pr=pr;
        this.value=value;
    }

    public List<T> getNext() {
        return next;
    }

    public T getValue() {
        return value;
    }

    public List<T> getPr() {
        return pr;
    }

    public void setNext(List<T> next) {
        this.next = next;
    }

    public void setPr(List<T> pr) {
        this.pr = pr;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
