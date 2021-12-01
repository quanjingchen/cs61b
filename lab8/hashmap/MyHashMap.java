package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Quanjing Chen quanjingchen@gmail.com
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

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
    private static final int INIT_CAPACITY = 16;
    private static final double INIT_loadFACTOR = 0.75;
    private Collection<Node>[] buckets; // array of bucket
    private int n;  // number of kay-value pairs
    private int m;  // number of buckets
    private double loadFactor;
    private Set<K> set;


    /** Constructors */
    public MyHashMap() {
        this(INIT_CAPACITY, INIT_loadFACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, INIT_loadFACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.m = initialSize;
        this.n = 0;
        this.loadFactor = maxLoad;
        set = new HashSet<>();

        buckets = new Collection[m];

        for (int i = 0; i < m; i ++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
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
        return new LinkedList<>();
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
        return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        this.n = 0;
        set = new HashSet<>();
        buckets = new Collection[m];

        for (int i = 0; i < m; i ++) {
            buckets[i] = createBucket();
        }
    }



    private int hash(K key) {
        if (key == null) {
            throw new UnsupportedOperationException();
        }else {
            return key.hashCode() & 0x7fffffff % m;
        }
    }

    @Override
    public boolean containsKey(K key) {
        return set.contains(key);
    }

    private V getHelper(K key, Collection<Node> list) {
        if (list == null) {
            return null;
        }

        for (Node node : list) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }

        return getHelper(key, buckets[hash(key)]);
    }



    @Override
    public int size() {
        return n;
    }

    private void putHelper(K key, V value, Collection<Node> list) {

        if (list != null) {
            for (Node node : list) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        }

        list.add(createNode(key, value));
        n += 1;
        set.add(key);
    }

    @Override
    public void put(K key, V value) {

        putHelper(key, value, buckets[hash(key)]);

        if ((double) n / m > loadFactor) {
            resize();
        }
    }

    private void resize() {
        MyHashMap<K, V> tmp = new MyHashMap<>(m * 2);
        Set<K> keys = keySet();
        for (K key : keys) {
            tmp.put(key,get(key));
        }
        m = tmp.m;
        buckets = tmp.buckets;
    }

    @Override
    public Set<K> keySet() {
        return set;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return set.iterator();
    }
}
