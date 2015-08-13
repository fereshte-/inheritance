package generateTest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Random;

import utils.LispReader;

public class PercyToTom {

	public void percy_toGeo(String fileName, String outName) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(outName);

		// detemine if it is a function application or a literal
		LispReader wholeFileLr = new LispReader(new FileReader(fileName));
		while(wholeFileLr.hasNext()){
			String wholeSentence = wholeFileLr.next();
			LispReader wholeSentenceLr = new LispReader(new StringReader(wholeSentence));
			wholeSentenceLr.next();	//passing the example
			String utterence = wholeSentenceLr.next();
			LispReader utterenceLr = new LispReader(new StringReader(utterence));
			utterenceLr.next();
			utterence = "";
			while(utterenceLr.hasNext())
				utterence += " " + utterenceLr.next();
			utterence = utterence.replaceAll("\"", "").trim();
			
			String original = wholeSentenceLr.next();
			LispReader originalLr = new LispReader(new StringReader(original));
			originalLr.next();
			original = "";
			while(originalLr.hasNext())
				original += " " + originalLr.next();	
			original = original.replaceAll("\"", "").trim();

			String formula = wholeSentenceLr.next();
			LispReader formulaLr = new LispReader(new StringReader(formula));
			formulaLr.next();
			formula = formulaLr.next();

			formula = formula.replaceAll("call edu.stanford.nlp.sempre.{1,15}.SimpleWorld.", "");
			formula = formula.replaceAll("call .size", "ccount");
			formula = formula.replaceAll("!type", "type");
			
			utterence = utterence.replace("?", " ");
			utterence = utterence.replace("-", " ");
			utterence = utterence.replace("'s", " 's");


			out.println(utterence + "\n" + formula + "\n\n" + original + "\n" + formula + "\n");

		}
		out.close();
	
	}
	
	public static void main(String[] args) throws IOException {
		PercyToTom percyToTom = new PercyToTom();
		
//		changeFormat.out = new PrintWriter("geo880-funql.test", "UTF-8");
//		changeFormat.outTest = new PrintWriter("geo880-funql.test", "UTF-8");
//		changeFormat.out1 = new PrintWriter("b.txt", "UTF-8");
//		changeFormat.out2 = new PrintWriter("a.txt", "UTF-8");

		percyToTom.percy_toGeo("/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/real_data_train.txt",
				"/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/train.txt");
		percyToTom.percy_toGeo("/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/real_data_test.txt",
				"/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/test.txt");

	}
}
