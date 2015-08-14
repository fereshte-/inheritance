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
			
			formula = formula.replace("en.meeting.weekly_standup", "weekly_standup:me");
			formula = formula.replace("en.meeting.annual_review", "annual_review:me");
			formula = formula.replace("en.location.greenberg_cafe", "greenberg_cafe:lo");
			formula = formula.replace("en.location.central_office", "central_office:lo");
			formula = formula.replace("en.person.alice", "alice:pe");
			formula = formula.replace("en.person.bob", "bob:pe");

			formula = formula.replace("en.hour", "hour:ho");
			formula = formula.replace("en.person", "person:pe");
			formula = formula.replace("en.meeting", "meeting:me");
			formula = formula.replace("en.location", "location:lo");
			
			
			out.println(utterence + "\n" + formula + "\n\n" + original + "\n" + formula + "\n");

		}
		out.close();
	
	}
	
	public static void main(String[] args) throws IOException {
		PercyToTom percyToTom = new PercyToTom();
		percyToTom.percy_toGeo("/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/real_data_train.txt",
				"/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/train.txt");
		percyToTom.percy_toGeo("/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/real_data_test.txt",
				"/Users/fereshte/Desktop/course/research/ubl/ubl_percy/data/test.txt");

	}
}
