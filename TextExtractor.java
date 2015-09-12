import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TextExtractor {

	public static void main(String[] args) throws IOException {

		String folderPath = args[0];
		String trainPath = args[1];

		File directory = new File(folderPath);

		for (File child : directory.listFiles()) {
			Path filePath = child.toPath();
			String filename = child.getName().substring(0, child.getName().indexOf("."));
			System.out.println(filename);
			String fileContent = (Files.readAllLines(filePath)).toString();
			Document htmlDoc = Jsoup.parse(fileContent);

			StringBuilder textToWrite = new StringBuilder();
			textToWrite.append(htmlDoc.select("span.legal-disclosures-line1").first().text());
			textToWrite.append(htmlDoc.select("span.fine-print-description").first().text());

			File newFile = new File(trainPath + "/" + filename + ".txt");
			try {
				Files.write(newFile.toPath(), String.valueOf(textToWrite).getBytes());
				System.out.println("Stored");
			} catch (IOException iox) {
				System.out.println("Failed to write file: " + filename + iox);
			}

		}

	}
}
