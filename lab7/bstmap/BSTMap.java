package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;

    private class BSTNode {
        private K key;               // sorted by key
        private V val;               // associated data
        private BSTNode left, right; // left and right subtrees
        private int size;            // number of nodes in subtree

        public BSTNode (K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BSTMap() {
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode x, K key) {
        if (x == null) {
            return false;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return containsKey(x.left, key);
        } else if (cmp > 0) {
            return containsKey(x.right, key);
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode x, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get(key) with a null key");
        }
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x.val;
        }
    }


    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode x) {
        if (x == null) {
            return 0;
        }
        return x.size;
    }

    @Override
    public void put(K key, V value) {

        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        root = put(root, key, value);

    }

    private BSTNode put(BSTNode x, K key, V value) {
        if (x == null) {
            return new BSTNode(key, value, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = put(x.left, key, value);
        } else if (cmp > 0) {
            x.right = put(x.right, key, value);
        } else {
            x.val = value;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    public K min() {
         return min(root);
    }

    private K min(BSTNode x) {
        if (x.left == null) return x.key;
        return min(x.left);
    }



    public void printInOrder() {

    }


}
