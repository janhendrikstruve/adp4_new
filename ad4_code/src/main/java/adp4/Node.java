package adp4;

public class Node<K extends Comparable<K>,E>
{
	K key;
	E element;
	Node<K,E> left;
	Node<K,E> right;

	public Node(K key, E element) {
		this.key = key;
		this.element = element;
	}
}
