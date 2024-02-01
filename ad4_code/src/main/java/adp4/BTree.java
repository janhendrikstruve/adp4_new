package adp4;

import java.util.function.Consumer;

public class BTree implements Comparable<BTree> {

	private HuffNode data;
	private BTree leftNode;
	private BTree rightNode;

	public BTree(HuffNode data) {
		this.data = data;
		this.leftNode = null;
		this.rightNode = null;
	}

	public HuffNode getData() {
		return data;
	}

	public void setData(HuffNode data) {
		if (data == null) {
			throw new IllegalArgumentException();
		}
		this.data = data;
	}


	public BTree getLeftNode() {
		return leftNode;
	}

	public BTree getRightNode() {
		return rightNode;
	}

	public void setRightNode(BTree btree) {
		if (btree == null) {
			throw new IllegalArgumentException();
		}
		rightNode = btree;
	}

	public void setLeftNode(BTree btree) {
		if (btree == null) {
			throw new IllegalArgumentException();
		}
		leftNode = btree;
	}


	public boolean isLeaf() {
		return leftNode == null && rightNode == null;
	}


	public void visitPostOrder(Consumer<BTree> visitor) {
		if (visitor == null) throw new IllegalArgumentException();
		if (leftNode != null) {
			leftNode.visitPostOrder(visitor);
		}
		if (rightNode != null) {
			rightNode.visitPostOrder(visitor);
		}
		visitor.accept(this);
	}

	public void visitInOrder(Consumer<BTree> visitor) {
		if (visitor == null) throw new IllegalArgumentException();
		if (leftNode != null) {
			leftNode.visitInOrder(visitor);
		}
		visitor.accept(this);
		if (rightNode != null) {
			rightNode.visitInOrder(visitor);
		}
	}

	public void visitPreOrder(Consumer<BTree> visitor) {
		if (visitor == null) throw new IllegalArgumentException();
		visitor.accept(this);
		if (leftNode != null) {
			leftNode.visitPreOrder(visitor);
		}
		if (rightNode != null) {
			rightNode.visitPreOrder(visitor);
		}
	}

	public int hashCode() {
		return data.getFrequency() * 256;
	}
	@Override
	public int compareTo(BTree t)
	{
		return Integer.compare(this.getData().getFrequency(), t.getData().getFrequency());
	}
}
