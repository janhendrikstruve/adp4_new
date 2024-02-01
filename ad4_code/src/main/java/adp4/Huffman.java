package adp4;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitInputFactory;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitOutputFactory;

import java.io.*;
import java.util.PriorityQueue;
import java.util.Stack;

public class Huffman
{
	static int chars = 256;
	private int[] freq;
	private String[] codeTable;
	private int count;
	private PriorityQueue<BTree> heap;
	private Stack<Integer> path;
	private BTree root;

	public Huffman() {
		freq = new int[chars];
		codeTable = new String[chars];
		heap = new PriorityQueue<>(BTree::compareTo);
		path = new Stack<Integer>();
		count = 0;
	}

	public void calculateCharacterFrequencies(String path) {
		try(InputStream input = new FileInputStream(path)) {

			int nextByte = input.read();
			while(nextByte != -1) {
				++count;
				++freq[nextByte];
				nextByte = input.read();
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not Found!");
		}
		catch (IOException io) {
			System.out.println(io.getMessage());
		}
	}

	public void buildHuffmanTree() {

		for (int i = 0; i < chars; ++i) {
			if (freq[i] > 0) {
				heap.add(new BTree(new HuffNode(i, freq[i])));
			}
		}

		while (heap.size() > 1) {
			BTree node1 = heap.remove();
			BTree node2 = heap.remove();
			BTree tree = new BTree(new HuffNode(-1, node1.getData().getFrequency() + node2.getData().getFrequency()));
			tree.setLeftNode(node1);
			tree.setRightNode(node2);
			heap.add(tree);
		}
		root = heap.remove();
	}

	public void calculateCodeFromHuffmanTree() {
		traverseHuffmanTree(root, "");
	}

	private void traverseHuffmanTree(BTree tree, String path) {
		if (tree.isLeaf()) {
			if(tree.getData().getCharacter() != -1) {
				codeTable[tree.getData().getCharacter()] = path;
			}
			return;
		}


		traverseHuffmanTree(tree.getLeftNode(), path + "0");
		traverseHuffmanTree(tree.getRightNode(), path + "1");

	}

	public void writeCharacterFrequencies() {
		for (int i = 0; i < chars; i++) {
			if (freq[i] > 0) {
				System.out.println("Character " + (char) i + ": Frequency = " + freq[i]);
			}
		}
	}

	public void encodeDataFile(String file) {
		calculateCharacterFrequencies(file);
		buildHuffmanTree();
		calculateCodeFromHuffmanTree();
		writeCharacterFrequencies();
		try (InputStream fileInput = new FileInputStream(file);
				OutputStream fileOutput = new FileOutputStream("enc_huff_" + file)){
			for(int i = 0; i < chars; ++i) {
				fileOutput.write(freq[i]);
			}
			BitOutput output = BitOutputFactory.from(fileOutput);
			int nextByte = fileInput.read();
			while (nextByte != -1) {
				String code = codeTable[nextByte];
				for(int i = 0; i < code.length(); ++i) {
					int bit = Character.getNumericValue(code.charAt(i));  // TODO: Aendern: vielleicht mit "int bit = (code.charAt(i) == '1') ? 1 : 0;"?
					output.writeInt(true, 1, bit);
				}
				nextByte = fileInput.read();
			}
			output.align(1);
		}
		catch (FileNotFoundException fe) {
			System.out.println("File not found!");
		}
		catch (IOException io) {

		}
	}

	public void decodeDataFile(String file) {

		try (InputStream fileInput = new FileInputStream(file);
				OutputStream fileOutput = new FileOutputStream("dec_huff_" + file)){
			for(int i = 0; i < chars; ++i) {
				freq[i] = fileInput.read();
			}
			BitInput input = BitInputFactory.from(fileInput);
			for (int i = 0; i < count; ++i) {
				fileOutput.write(decodeR(root, input));
			}
		}
		catch (FileNotFoundException fe) {
			System.out.println("File not found!");
		}
		catch (IOException io) {
			System.out.println("Bit reading failed!");
		}

	}

	private int decodeR(BTree tree, BitInput input) throws IOException
	{
		if (tree.isLeaf()) {
			return tree.getData().getCharacter();
		}
		int bit = input.readInt(true, 1);
		if (bit == 0) {
			return decodeR(tree.getLeftNode(), input);
		}
		else {
			return decodeR(tree.getRightNode(), input);
		}

	}

	public void readCharacterFrequencies() {

	}

	public int readCharacterCount() {
		return 0;
	}
}
