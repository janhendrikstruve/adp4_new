package adp4;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitInputFactory;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitOutputFactory;

import java.io.*;

public class LZW
{
	private BSTree<Integer, String> decTree;
	private BSTree<String, Integer> encTree;

	static int TOKEN_SIZE = 10;

	private int maxDictSize;


	public LZW() {
		decTree = new BSTree<Integer, String>();
		encTree = new BSTree<String, Integer>();
		maxDictSize = (int)Math.pow(2, TOKEN_SIZE);
	}

	public void compress(String file) {
		try (InputStream fileInput = new FileInputStream(file);
				OutputStream fileOutput = new FileOutputStream("enc_lzw_" + file)){
			for (int i = 0; i < 256; ++i) {
				encTree.insert("" + (char) i, i);
			}
			BitOutput output = BitOutputFactory.from(fileOutput);

			int nextByte = 0;
			StringBuilder current = new StringBuilder();
			String word = "";
			char nextChar;
			boolean readNext = true;
			while (true) {
				if (readNext) {
					nextByte = fileInput.read();
					if (nextByte == - 1) break;
				}
				nextChar = (char)nextByte;
				if (readNext) {
					word = current.toString() + nextChar;
				}
				readNext = true;
				if (encTree.contains(word)) {
					current = new StringBuilder(word);
				}
				else {
					if (maxDictSize > encTree.size()) {
						output.writeInt(true, TOKEN_SIZE, encTree.get(current.toString()));
						encTree.insert(word, encTree.size());
						current = new StringBuilder("" + nextChar);
					}
					else {
						readNext = false;
						int counter = 1;
						String currentS = String.valueOf(word.charAt(0));
						while (encTree.contains(currentS)) {
							currentS += word.charAt(counter);
							++counter;
						}
						output.writeInt(true, TOKEN_SIZE, encTree.get(currentS.substring(0, currentS.length() - 1)));
						word = word.substring(counter - 1);
					}
				}
			}

			if (!word.isEmpty()) {
				output.writeInt(true, TOKEN_SIZE, encTree.get(current.toString()));
			}
			output.align(1);
		}
		catch (FileNotFoundException fe) {
			System.out.println("File not found!");
		}
		catch (IOException io) {

		}

	}

	public void decompress(String file) {
		try (InputStream fileInput = new FileInputStream(file);
				OutputStream fileOutput = new FileOutputStream("dec_lzw_" + file)){
			for (char i = 0; i < 256; ++i) {
				decTree.insert((int)i, Character.valueOf(i).toString());
			}

			BitInput input = BitInputFactory.from(fileInput);
			BitOutput output = BitOutputFactory.from(fileOutput);
			int iInput = input.readInt(true, TOKEN_SIZE);
			int prevInput = iInput;
			String lastWritten, newEntry;
			String toWrite = decTree.get(iInput);
			for (int i = 0; i < toWrite.length(); ++i) {
				output.writeChar(8, toWrite.charAt(i));
			}

			while (fileInput.available() > 0) {
				iInput = input.readInt(true, TOKEN_SIZE);
				if (decTree.contains(iInput)) {
					toWrite = decTree.get(iInput);
				}
				else {
					toWrite = decTree.get(prevInput) + toWrite.charAt(0);
				}

				if (maxDictSize > decTree.size()) {
					newEntry = decTree.get(prevInput) + toWrite.charAt(0);
					decTree.insert(decTree.size(), newEntry);
				}

				if(!toWrite.isEmpty()) {
					for (int i = 0; i < toWrite.length(); ++i) {
						output.writeChar(8, toWrite.charAt(i));
					}
				}
				prevInput = iInput;
			}
			System.out.println(decTree.size() + " Wörter");
			output.align(1);
			calculateStringLength();
		}
		catch (FileNotFoundException fe) {
			System.out.println("File not found!");
		}
		catch (IOException io) {

		}
	}

	public void calculateStringLength() {
		double mean = 0;
		for (int i = 0; i < decTree.size(); ++i) {
			mean += decTree.get(i).length();
		}
		System.out.println("Durschnittliche Codierte Stringlänge: " + mean/decTree.size());
	}
}