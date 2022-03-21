package lesson4;

import java.util.*;
import kotlin.NotImplementedError;
import lesson3.BinarySearchTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map.Entry;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {

    private static class Node {
        SortedMap<Character, Node> children = new TreeMap<>();
    }

    private final Node root = new Node();

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        for (char character : withZero(element).toCharArray()) {
            Node child = current.children.get(character);
            if (child != null) {
                current = child;
            } else {
                modified = true;
                Node newChild = new Node();
                current.children.put(character, newChild);
                current = newChild;
            }
        }
        if (modified) {
            size++;
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element);
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            return true;
        }
        return false;
    }


    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    @NotNull
    @Override
    public Iterator<String> iterator() { return new TrieIterator(); }

    public class TrieIterator implements Iterator<String> {
        private final Deque<Iterator<Entry<Character, Node>>> deq = new ArrayDeque<>();
        private final StringBuilder sb = new StringBuilder();
        private String lastNext;



        private TrieIterator() {
            if (Trie.this.size != 0) { deq.push(root.children.entrySet().iterator()); }
        }

        private void getNext() {
            Iterator<Entry<Character, Node>> iter = deq.peek();
            while (iter != null) {
                while (iter.hasNext()) {
                    Entry<Character, Node> n = iter.next();
                    if (n.getKey() == (char) 0) {
                        lastNext = sb.toString();
                        return;
                    }
                    sb.append(n.getKey());
                    iter = n.getValue().children.entrySet().iterator();
                    deq.push(iter);
                }
                deq.remove();
                if (sb.length() != 0) sb.deleteCharAt(sb.length() - 1);
                iter = deq.peek();
            }
        }

//      R = O(1), T = O(N)
        @Override
        public boolean hasNext() {
            removeEmptyDeq(); // O(N)
            return !deq.isEmpty();
        }


//      R = O(1), T = O(N)
        @Override
        public String next() {
            if (!hasNext()) throw new NoSuchElementException();
            getNext();
            return lastNext;
        }

//      T = R = O(1)
        @Override
        public void remove() {
            if (lastNext == null) throw new IllegalStateException();
            deq.peek().remove();
            lastNext = null;
            size--;
        }

        private void removeEmptyDeq() {
            while (deq.peek() != null && !deq.peek().hasNext()) {
                if (sb.length() != 0) sb.deleteCharAt(sb.length() - 1);
                deq.remove();
            }
       }
    }

}