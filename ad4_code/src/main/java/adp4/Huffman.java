package adp4;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.BitInputFactory;
import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.BitOutputFactory;

import java.io.*;
import java.util.PriorityQueue;
import java.util.Stack;

public class Huffman {
	static final int CHARS = 256; // Anzahl der möglichen Zeichen (ASCII)
	private int[] freq; // Häufigkeit jedes Zeichens
	private String[] codeTable; // Huffman-Codes für jedes Zeichen
	private PriorityQueue<BTree> heap; // Prioritätsqueue zur Erstellung des Huffman-Baums
	private Stack<Integer> path; // Pfadverfolgung (wird hier nicht verwendet)
	private BTree root; // Wurzel des Huffman-Baums
	private int count; // Gesamtanzahl der gelesenen Zeichen

	public Huffman() {
		freq = new int[CHARS]; // Initialisierung der Häufigkeitstabelle
		codeTable = new String[CHARS]; // Initialisierung der Code-Tabelle
		heap = new PriorityQueue<>(BTree::compareTo); // Initialisierung der Prioritätsqueue
		path = new Stack<Integer>(); // Initialisierung des Stack für den Pfad
		count = 0; // Initialisierung des Zeichenzählers
	}

	// Kodiert eine Datei mit den generierten Huffman-Codes <1>
	public void encodeDataFile(String file) {
		calculateCharacterFrequencies(file); // Berechnung der Zeichenhäufigkeiten
		buildHuffmanTree(); // Erstellung des Huffman-Baums
		calculateCodeFromHuffmanTree(); // Generierung der Huffman-Codes
		writeCharacterFrequencies(); // Ausgabe der Zeichenhäufigkeiten (optional)
		try (InputStream fileInput = new FileInputStream(file);
			 OutputStream fileOutput = new FileOutputStream("enc_huff_" + file)){
			for(int i = 0; i < CHARS; ++i) {
				fileOutput.write(freq[i]); // Schreiben der Zeichenhäufigkeiten am Anfang der kodierten Datei
			}
			BitOutput output = BitOutputFactory.from(fileOutput); // Erstellung eines Bit-Output-Streams
			int nextByte = fileInput.read(); // Lesen des ersten Zeichens
			while (nextByte != -1) { // Solange das Ende der Datei nicht erreicht ist
				String code = codeTable[nextByte]; // Abruf des Codes für das Zeichen
				for(int i = 0; i < code.length(); ++i) { // Für jedes Bit im Code
					int bit = (code.charAt(i) == '1') ? 1 : 0;// Umwandlung in ein Bit
					output.writeInt(true, 1, bit); // Schreiben des Bits
				}
				nextByte = fileInput.read(); // Lesen des nächsten Zeichens
			}
			output.align(1); // Auffüllen des letzten Bytes, falls nötig
		}
		catch (FileNotFoundException fe) { // Datei nicht gefunden
			System.out.println("File not found!");
		}
		catch (IOException io) { // E/A-Fehler
			// Fehlerbehandlung hier
		}
	}

	// Berechnet die Häufigkeit jedes Zeichens in einer Datei <2>
	// schreibt in freq und count
	public void calculateCharacterFrequencies(String path) {
		try(InputStream input = new FileInputStream(path)) {
			int nextByte = input.read(); // Lesen des ersten Zeichens, maybe nicht best practice?
			while(nextByte != -1) { // Solange das Ende der Datei nicht erreicht ist
				++count; // Erhöhung des Gesamtzählers
				++freq[nextByte]; // Erhöhung der Häufigkeit des gelesenen Zeichens
				nextByte = input.read(); // Lesen des nächsten Zeichens
			}
		}
		catch (FileNotFoundException e) { // Datei nicht gefunden
			System.out.println("File not Found!");
		}
		catch (IOException io) { // E/A-Fehler
			System.out.println(io.getMessage());
		}
	}

	// Erstellt den Huffman-Baum basierend auf den Zeichenhäufigkeiten <3>
	public void buildHuffmanTree() {
		for (int i = 0; i < freq.length; ++i) { // Für jedes mögliche Zeichen
			if (freq[i] > 0) { // Wenn das Zeichen in der Datei vorkommt
				heap.add(new BTree(new HuffNode(i, freq[i]))); // Erstellung eines (Teil-)Baums für das Zeichen
			}
		}

		while (heap.size() > 1) { // Solange mehr als ein Baum in der Queue ist
			BTree node1 = heap.remove(); // Entfernen des Baums mit der geringsten Häufigkeit
			BTree node2 = heap.remove(); // Entfernen des nächsten Baums
			// Kombination der beiden Bäume zu einem neuen Baum
			BTree tree = new BTree(new HuffNode(-1, node1.getData().getFrequency() + node2.getData().getFrequency()));
			tree.setLeftNode(node1); // Setzen des linken Kindes
			tree.setRightNode(node2); // Setzen des rechten Kindes
			heap.add(tree); // Hinzufügen des neuen Baums zur Queue
		}
		root = heap.remove(); // Der verbleibende Baum ist der Huffman-Baum
	}

	/*public void calculateCharacterFrequencies(String path) {
            try (InputStream inputStream = new FileInputStream(path)) {
                // Erstellen eines BitInput-Objekts zum bitweisen Lesen
                BitInput inputFile = BitInputFactory.from(inputStream);

                // Endlosschleife zum Lesen der Bits
                while (true) {
                    } catch (EOFException e) {
                        // Beenden der Schleife, wenn das Ende der Datei erreicht ist
                        break;
                    }
                }
            }
        }*/

	// Generiert die Huffman-Codes für jedes Zeichen
	public void calculateCodeFromHuffmanTree() {
		traverseHuffmanTree(root, ""); // Start der Tiefensuche vom Wurzelknoten
	}

	// Durchläuft den Huffman-Baum und weist jedem Zeichen einen Code zu
	private void traverseHuffmanTree(BTree tree, String path) {
		if (tree.isLeaf()) { // Wenn ein Blatt erreicht wird
			if(tree.getData().getCharacter() != -1) { // Wenn das Blatt kein Pseudozeichen ist
				codeTable[tree.getData().getCharacter()] = path; // Zuweisung des Pfades als Code
			}
			return;
		}

		// Rekursive Durchläufe für linke (0) und rechte (1) Kindknoten
		traverseHuffmanTree(tree.getLeftNode(), path + "0"); // Hinzufügen einer '0' für den linken Pfad
		traverseHuffmanTree(tree.getRightNode(), path + "1"); // Hinzufügen einer '1' für den rechten Pfad
	}

	// Ausgabe der Zeichenhäufigkeiten - Hilfsmethode zur Überprüfung
	public void writeCharacterFrequencies() {
		for (int i = 0; i < CHARS; i++) { // Für jedes mögliche Zeichen
			if (freq[i] > 0) { // Wenn das Zeichen vorkommt
				System.out.println("Character " + (char) i + ": Frequency = " + freq[i]); // Ausgabe der Häufigkeit
			}
		}
	}

	// Dekodiert eine mit Huffman-Codes kodierte Datei
	public void decodeDataFile(String file) {
		try (InputStream fileInput = new FileInputStream(file);
			 OutputStream fileOutput = new FileOutputStream("dec_huff_" + file)){
			for(int i = 0; i < CHARS; ++i) {
				freq[i] = fileInput.read(); // Lesen der Zeichenhäufigkeiten
			}
			BitInput input = BitInputFactory.from(fileInput); // Erstellung eines Bit-Input-Streams
			for (int i = 0; i < count; ++i) { // Für jedes Zeichen in der Eingabedatei
				fileOutput.write(decodeR(root, input)); // Rekursives Dekodieren jedes Zeichens
			}
		}
		catch (FileNotFoundException fe) { // Datei nicht gefunden
			System.out.println("File not found!");
		}
		catch (IOException io) { // E/A-Fehler
			System.out.println("Bit reading failed!");
		}
	}

	// Rekursive Methode zum Dekodieren eines Zeichens aus dem Huffman-Baum
	private int decodeR(BTree tree, BitInput input) throws IOException {
		if (tree.isLeaf()) { // Wenn ein Blatt erreicht wird
			return tree.getData().getCharacter(); // Rückgabe des Zeichens des Blattes
		}
		int bit = input.readInt(true, 1); // Lesen eines Bits
		if (bit == 0) { // Wenn das Bit 0 ist
			return decodeR(tree.getLeftNode(), input); // Gehe zum linken Kindknoten
		}
		else { // Wenn das Bit 1 ist
			return decodeR(tree.getRightNode(), input); // Gehe zum rechten Kindknoten
		}
	}

	public void readCharacterFrequencies() {
		// Methode zur Einlesung der Zeichenhäufigkeiten (nicht implementiert)
	}

	public int readCharacterCount() {
		// Methode zur Rückgabe der Gesamtanzahl der Zeichen (nicht implementiert)
		return 0;
	}
}
