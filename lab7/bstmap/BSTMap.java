package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public  class BSTMap <K extends Comparable<K>, V> implements Map61B <K, V>{
    private int  size=0;

    private BSTNode root=null;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left,right;
        public BSTNode(K key, V val){
            this.key = key;
            this.val = val;
        }

    }
    public BSTMap (){}
    @Override
    public void clear() {
        root=null;
        size=0;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode temp=root;
        while (temp!=null){
            if (temp.key.compareTo(key)==0){
                return true;
            }else if (temp.key.compareTo(key)>0){
                temp=temp.left;
            }else {
                temp=temp.right;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        BSTNode temp=root;
        while (temp!=null){
            if (temp.key.compareTo(key)==0){
                return temp.val;
            }else if (temp.key.compareTo(key)>0){
                temp=temp.left;
            }else {
                temp=temp.right;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        size++;
        BSTNode temp=root;
        while (true){
            if (temp==null){
                root = new BSTNode(key,value);
                return;
            }else if (temp.key.compareTo(key)>0){
                if (temp.left==null){
                    temp.left=new BSTNode(key,value);
                    return;
                }else {
                    temp=temp.left;
                }
            }else {
                if (temp.right==null){
                    temp.right=new BSTNode(key,value);
                    return;
                }else {
                    temp=temp.right;
                }
            }
        }
    }

    @Override
    public Set<K> keySet() {
        if(root == null){
            return null;
        }
        Set<K> set = new HashSet<>();
        addKey(root,set);
        return set;
    }
    private void addKey(BSTNode node, Set<K>set){
        if(node == null){
            return ;
        }
        set.add(node.key);
        addKey(node.left,set);
        addKey(node.right,set);
    }

    @Override
    public V remove(K key) {
        BSTNode temp=root;
        BSTNode parent=root;
        while (temp!=null){
            if (temp.key.compareTo(key)==0){
                V value=temp.val;
                if (parent.left==temp){
                    if (temp.left==null){
                        parent.left=temp.right;
                    }else if (temp.right==null){
                        parent.left=temp.left;
                    }else {
                        BSTNode max;
                        if (temp.left.right!=null){
                            max =this.removeMaxAndReturnMax(temp.left);
                        }else {
                            max=temp.left;
                            temp.left=max.left;
                        }
                        parent.left=max;
                        max.left=temp.left;
                        max.right=temp.right;
                    }
                }else if (parent.right==temp) {
                    if (temp.left==null){
                        parent.right=temp.right;
                    }else if (temp.right==null){
                        parent.right=temp.left;
                    }else {
                        BSTNode max;
                        if (temp.left.right!=null){
                            max =this.removeMaxAndReturnMax(temp.left);
                        }else {
                            max=temp.left;
                            temp.left=max.left;
                        }
                        parent.right=max;
                        max.left=temp.left;
                        max.right=temp.right;
                    }
                }else {
                    if (root.left==null){
                        root=root.right;
                    }else if (root.right==null) {
                        root=root.left;
                    }else {
                        BSTNode max;
                        if (root.left.right!=null){
                            max =this.removeMaxAndReturnMax(root.left);
                        }else {
                            max=root.left;
                            root.left=max.left;
                        }
                        max.left=root.left;
                        max.right=root.right;
                        root=max;
                    }
                }
                size--;
                return value;
            }else if (temp.key.compareTo(key)>0){
                parent=temp;
                temp=temp.left;

            }else {
                parent=temp;
                temp=temp.right;

            }
        }
        return null;
    }
    //max不能是node
    private BSTNode removeMaxAndReturnMax(BSTNode node){
        BSTNode temp = node;
        BSTNode parent =temp;
        while (temp.right!=null){
            parent=temp;
            temp=temp.right;
        }
        parent.right=temp.left;
        temp.right=null;
        temp.left=null;
        return temp;
    }


    @Override
    public V remove(K key, V value) {
        if (this.get(key).equals(value)){
            return this.remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        Set<K> set=this.keySet();
        return set.iterator();
    }
}
