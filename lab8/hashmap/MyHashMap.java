package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */

public class MyHashMap<K, V> implements Map61B<K, V> {
    private int initialSize ;
    private double loadFactor ;
    private int size;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this.initialSize=16;
        this.loadFactor=0.75;
        this.size=0;
        this.buckets=this.createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this();
        this.initialSize=initialSize;
        this.buckets=this.createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this();
        this.initialSize =initialSize;
        this.loadFactor = maxLoad;
        this.buckets=this.createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private int getBucketIndex(K key){
        if (key.hashCode()<0)return -key.hashCode()%initialSize;
        return key.hashCode()%initialSize;
    }


    @Override
    public void clear() {
        this.size=0;
        this.buckets=createTable(initialSize);
    }


    @Override
    public boolean containsKey(K key) {
        Collection<Node> temp=this.buckets[getBucketIndex(key)];
        if (temp==null)return false;
        for (Node node:temp
             ) {
            if (node.key.equals(key))return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        Collection<Node> temp=this.buckets[getBucketIndex(key)];
        if (temp==null)return null;
        for (Node node:temp
        ) {
            if (node.key.equals(key))return node.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        this.doubleBucketSize();
        Collection<Node> temp=this.buckets[getBucketIndex(key)];
        if (temp==null){
            this.buckets[getBucketIndex(key)]=createBucket();
            temp=this.buckets[getBucketIndex(key)];
        }
        Node tempNode=this.createNode(key,value);
        for (Node node:temp) {
            if (node.key.equals(key)){
                node.value=value;
                return;
            }
        }
        size++;
        temp.add(tempNode);
    }
    @Override
    public Set<K> keySet() {
        Set<K> returnSet = new HashSet<>();
        for (Collection<Node> temp:buckets
             ) {
            if (temp==null)continue;
            for (Node temp2:temp
                 ) {
                returnSet.add(temp2.key);
            }
        }
        return returnSet;
    }

    @Override
    public V remove(K key) {
        Collection<Node> temp=this.buckets[getBucketIndex(key)];
        V value=this.get(key);
        boolean flag=temp.removeIf(node -> node.key .equals(key));
        if (flag)size--;
        return value;
    }

    @Override
    public V remove(K key, V value) {
        Collection<Node> temp=this.buckets[getBucketIndex(key)];
        boolean flag=temp.removeIf(node -> node.key .equals(key) &&node.value.equals(value));
        if (flag){
            size--;
            return value;
        }else {
            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }



    private void doubleBucketSize(){
        if (1.0*size/initialSize<loadFactor)return;
        this.initialSize=initialSize*2;
        Collection[] temp=this.buckets;
        this.clear();
        for (Collection<Node> temp2:temp
        ) {
            if (temp2==null)continue;
            for (Node temp3:temp2
            ) {
                this.put(temp3.key,temp3.value);
            }
        }

    }

}
