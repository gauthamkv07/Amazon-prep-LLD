// Interface for eviction policies

import java.util.HashMap;
import java.util.Map;

interface EvictionPolicy {
    void recordAccess(int key);
    void removeLeastUsed();
}

class Node {
    int key, value;
    Node prev, next;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

// A list to manage nodes
class DoublyLinkedList {
    Node head, tail;
    int size;

    public DoublyLinkedList() {
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        size = 0;
        head.next = tail;
        tail.prev = head;
    }

    public void addNode(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    public void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    public Node removeTail() {
        Node node = tail.prev;
        if (node != head) {
            removeNode(node);
        }
        return node;
    }
}

// Eviction policy for Least Frequently Used (LFU)
class LFUEvictionPolicy implements EvictionPolicy {
    Map<Integer, DoublyLinkedList> freqMap = new HashMap<>();
    Map<Integer, Node> keyNodeMap = new HashMap<>();
    Map<Integer, Integer> keyFreqMap = new HashMap<>();
    int minFreq = 0;

    @Override
    public void recordAccess(int key) {
        int freq = keyFreqMap.get(key);
        DoublyLinkedList list = freqMap.get(freq);
        Node node = keyNodeMap.get(key);

        // Move the node to the higher frequency list
        list.removeNode(node);
        if (list.size == 0 && minFreq == freq) minFreq++;

        keyFreqMap.put(key, freq + 1);
        freqMap.computeIfAbsent(freq + 1, k -> new DoublyLinkedList()).addNode(node);
    }

    @Override
    public void removeLeastUsed() {
        // Remove from the least frequently used list
        DoublyLinkedList leastFreqList = freqMap.get(minFreq);
        Node nodeToRemove = leastFreqList.removeTail();

        if (nodeToRemove != null) {
            keyNodeMap.remove(nodeToRemove.key);
            keyFreqMap.remove(nodeToRemove.key);
        }
    }
}

// Main Cache class with dependency on eviction policy
class LFUCache {
    private final int capacity;
    private int size = 0;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final LFUEvictionPolicy evictionPolicy;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.evictionPolicy = new LFUEvictionPolicy();
    }

    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        evictionPolicy.recordAccess(key);
        return cache.get(key).value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        if (cache.containsKey(key)) {
            cache.get(key).value = value;
            evictionPolicy.recordAccess(key);
            return;
        }

        if (size >= capacity) {
            evictionPolicy.removeLeastUsed();
            size--;
        }

        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        evictionPolicy.recordAccess(key);
        size++;
    }
}
