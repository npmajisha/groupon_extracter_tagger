
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.InvalidFormatException;

public class Tagger {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws InvalidFormatException, IOException {

		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(),
				"invertible=true");
		String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);

		Path filepath = Paths.get("models/en-chunker.bin");
		InputStream is = new FileInputStream(filepath.toFile());
		ChunkerModel cModel = new ChunkerModel(is);
		ChunkerME chunkerME = new ChunkerME(cModel);

		File combinedText = new File(args[0]);
		FileReader fileReader = new FileReader(combinedText);

		// Output file
		File outputForChunking = new File(args[1]);
		FileWriter fout = new FileWriter(outputForChunking);
		PrintWriter printWriter = new PrintWriter(fout);

		DocumentPreprocessor dp = new DocumentPreprocessor(fileReader);
		dp.setTokenizerFactory(tokenizerFactory);

		int count = 0;

		for (List line : dp) {
			String sentence = Sentence.listToString(line);
			System.out.println();
			List<CoreLabel> tokens = (tokenizerFactory.getTokenizer(new StringReader(sentence))).tokenize();

			Tree tree = parser.apply(tokens);

			List<Word> word_list = tree.yieldWords();
			List<String> word_tokens = new ArrayList<String>();
			for (Word word : word_list) {
				word_tokens.add(word.word());
			}
			String[] words = new String[word_tokens.size()];
			words = word_tokens.toArray(words);

			List<TaggedWord> postags = tree.taggedYield();
			List<String> tag_tokens = new ArrayList<String>();
			for (TaggedWord postag : postags) {
				tag_tokens.add(postag.tag());
			}
			String[] tags = new String[tag_tokens.size()];
			tags = tag_tokens.toArray(tags);

			String result[] = chunkerME.chunk(words, tags);

			count++;
			if (count == 200) {
				printWriter.println("Test");
			}

			for (int i = 0; i < words.length; i++) {
				System.out.println(words[i] + "\t" + tags[i] + "\t" + result[i]);
				printWriter.println(words[i] + "\t" + tags[i] + "\t" + result[i]);
			}
			printWriter.println();

		}

		printWriter.close();

	}

}
