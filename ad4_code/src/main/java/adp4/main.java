package adp4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class main
{
	public static void main (String[] args) {
		if (args[0].equals("huffman")) {
			Huffman huffman = new Huffman();
			huffman.encodeDataFile("ad_vl.pdf");
			huffman.decodeDataFile("enc_huff_ad_vl.pdf");
			huffman = new Huffman();
			huffman.encodeDataFile("List.use");
			huffman.decodeDataFile("enc_huff_List.use");
			huffman = new Huffman();
			huffman.encodeDataFile("shakespeare.txt");
			huffman.decodeDataFile("enc_huff_shakespeare.txt");
			huffman = new Huffman();
			huffman.encodeDataFile("bewerbung.docx");
			huffman.decodeDataFile("enc_huff_bewerbung.docx");
			huffman = new Huffman();
			huffman.encodeDataFile("7zip.exe");
			huffman.decodeDataFile("enc_huff_7zip.exe");
		}
		else if (args[0].equals("lzw")){

			LZW lzw = new LZW();
			lzw.compress("ad_vl.pdf");
			lzw.decompress("enc_lzw_ad_vl.pdf");
			lzw = new LZW();
			lzw.compress("List.use");
			lzw.decompress("enc_lzw_List.use");
			lzw = new LZW();
			lzw.compress("shakespeare.txt");
			lzw.decompress("enc_lzw_shakespeare.txt");
			lzw = new LZW();
			lzw.compress("bewerbung.docx");
			lzw.decompress("enc_lzw_bewerbung.docx");
			lzw = new LZW();
			lzw.compress("7zip.exe");
			lzw.decompress("enc_lzw_7zip.exe");
		}
		else if (args[0].equals("benchmark")) trackAndSavePerformanceData();
	}

	// Methode zum Erfassen der Performance-Daten
	public static void trackAndSavePerformanceData() {
		String[] fileNames = {"ad_vl.pdf", "List.use", "shakespeare.txt", "bewerbung.docx", "7zip.exe"};
		String[] algorithms = {"huffman", "lzw"};

		for (String algorithm : algorithms) {
			String csvOutput = algorithm + "_performance.csv";
			try (FileWriter csvWriter = new FileWriter(csvOutput)) {
				// Schreiben der CSV-Header
				csvWriter.append("Dateiname,Originalgröße (Bytes),Kodierte Größe (Bytes),Kodierungszeit (ms),Dekodierungszeit (ms)\n");

				for (String fileName : fileNames) {
					long startTime, endTime, encodeTime, decodeTime;
					long originalSize, encodedSize;
					File originalFile = new File(fileName);
					originalSize = originalFile.length();
					File encodedFile;

					// Ausführen der Kodierung und Dekodierung basierend auf dem Algorithmus
					if (algorithm.equals("huffman")) {
						Huffman huffman = new Huffman();
						startTime = System.currentTimeMillis();
						huffman.encodeDataFile(fileName);
						endTime = System.currentTimeMillis();
						encodeTime = endTime - startTime;
						encodedFile = new File("enc_huff_" + fileName);
						encodedSize = encodedFile.length();

						startTime = System.currentTimeMillis();
						huffman.decodeDataFile("enc_huff_" + fileName);
						endTime = System.currentTimeMillis();
					} else { // LZW
						LZW lzw = new LZW();
						startTime = System.currentTimeMillis();
						lzw.compress(fileName);
						endTime = System.currentTimeMillis();
						encodeTime = endTime - startTime;
						encodedFile = new File("enc_lzw_" + fileName);
						encodedSize = encodedFile.length();

						startTime = System.currentTimeMillis();
						lzw.decompress("enc_lzw_" + fileName);
						endTime = System.currentTimeMillis();
					}
					decodeTime = endTime - startTime;

					// Speichern der Daten im CSV
					csvWriter.append(String.join(",", fileName, String.valueOf(originalSize), String.valueOf(encodedSize), String.valueOf(encodeTime), String.valueOf(decodeTime) + "\n"));
				}
			} catch (IOException e) {
				System.err.println("Fehler beim Schreiben der CSV-Datei: " + e.getMessage());
			}
		}
	}
}
