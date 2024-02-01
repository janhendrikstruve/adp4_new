package adp4;

import java.util.NoSuchElementException;

public class BSTree<K extends Comparable<K>,E> {

    private Node<K,E> head;

    public BSTree() {
        head = null;
    }

    public void insert(K k, E e) {
        head = insertR(head, k, e);
    }

    private Node<K,E> insertR(Node<K,E> n, K key, E element) {
        if (n == null) {
            return new Node<>(key,element);
        }

        if (n.key.compareTo(key) > 0) {
            n.left = insertR(n.left, key, element);
        } else {
            n.right = insertR(n.right, key, element);
        }
        return n;
    }


    public void remove(K k) throws NoSuchElementException {
        head = removeR(head,k);
    }

    private Node<K,E> removeR(Node<K,E> n, K key) {
        if (n == null) {
            return null;
        }

        if (n.key.compareTo(key) > 0) {
            n.left = removeR(n.left, key);
        }
        else if (n.key.compareTo(key) < 0) {
            n.right = removeR(n.right, key);
        }
        else {
            if (n.right == null) {
                return n.left;
            }
            else if (n.left == null) {
                return n.right;
            }
            else {
                Node<K, E> node = n.right;
                while (node.left != null) {
                    node = node.left;
                }
                n.key = node.key;
                n.element = node.element;
                n.right = removeR(n.right, n.key);
            }
        }
        return n;
    }

    public E get(K k) throws NoSuchElementException {
        return getR(head, k);
    }

    private E getR(Node<K, E> n, K key) throws NoSuchElementException{
        if (n == null) {
            throw new NoSuchElementException();
        }
        if (n.key.compareTo(key) == 0) {
            return n.element;
        }
        else if (n.key.compareTo(key) > 0) {
            return getR(n.left, key);
        }
        else {
            return getR(n.right, key);
        }
    }

    public int size() {
        return sizeR(head);
    }

    private int sizeR(Node n) {
        if (n == null) {
            return 0;
        }
        else {
            return 1 + sizeR(n.left) + sizeR(n.right);
        }
    }


    public boolean contains(K k) {
        return containsR(head, k);
    }

    private boolean containsR(Node n, K key) {
        if (n == null) {
            return false;
        }
        if (n.key.compareTo(key) == 0) {
            return true;
        }
        if (n.key.compareTo(key) > 0) {
            return containsR(n.left, key);
        }
        else {
            return containsR(n.right, key);
        }
    }

    public int path(K k) {
        return pathR(head, k);
    }

    private int pathR(Node n, K key) {
        int length = 0;

        if (n.key.compareTo(key) == 0) {
            return length;
        }
        if (n.key.compareTo(key) > 0) {
            return 1 + pathR(n.left, key);
        }
        else {
            return 1 + pathR(n.right, key);
        }

    }
}
