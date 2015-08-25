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
			utterence = utterence.replace("weekly standup", "weekly_standup");
			utterence = utterence.replace("annual review", "annual_review");
			utterence = utterence.replace("greenberg cafe", "greenberg_cafe");
			utterence = utterence.replace("central office", "central_office");
			
			utterence = utterence.replace("multivariate data analysis",
					"multivariate_data_analysis");
			utterence = utterence.replace("annals of statistics", "annals_of_statistics");
			utterence = utterence.replace("computational linguistics", "computational_linguistics");


			
			
// calandar
			
			formula = formula.replace("en.meeting.weekly_standup", "weekly_standup:me");
			formula = formula.replace("en.meeting.annual_review", "annual_review:me");
			formula = formula.replace("en.location.greenberg_cafe", "greenberg_cafe:lo");
			formula = formula.replace("en.location.central_office", "central_office:lo");
			formula = formula.replace("en.person.alice", "alice:pe");
			formula = formula.replace("en.person.bob", "bob:pe");


			formula = formula.replace("en.hour ", "hour:tyho ");
			formula = formula.replace("en.person ", "person:type ");
			formula = formula.replace("en.meeting ", "meeting:tyme ");
			formula = formula.replace("en.location ", "location:tylo ");

//publication
			
			formula = formula.replace("en.article.multivariate_data_analysis",
					"multivariate_data_analysis:ar");
			formula = formula.replace("en.venue.annals_of_statistics",
					"annals_of_statistics:ve");
			formula = formula.replace("en.venue.computational_linguistics",
					"computational_linguistics:ve");
			formula = formula.replace("en.person.efron", "efron:pe");
			formula = formula.replace("en.person.lakoff", "lakoff:pe");


			formula = formula.replace("en.article", "article:tyar");
			formula = formula.replace("en.person", "person:type");
			formula = formula.replace("en.venue", "venue:tyve");
			
		//	formula = formula.replace("concat", "or");

			if(
					formula.contains("<=") || formula.contains(">=") ||
					formula.contains("countS") || formula.contains("countC") 
					|| formula.contains("min") || formula.contains("max")
					|| formula.contains("count")
					) continue;
			
			if(outName.contains("test")){
				out.println(utterence + "\n" + formula + "\n");
			}else{
				out.println(utterence + "\n" + formula + "\n");
		//		out.println(original + "\n" + formula + "\n");
			}
		//	out.println(original + "\n" + formula + "\n");

		}
		out.close();
	
	}
	
	public static void main(String[] args) throws IOException {
		PercyToTom percyToTom = new PercyToTom();
		percyToTom.percy_toGeo("./data/real_train_both.txt",
				"./data/train.txt");
		percyToTom.percy_toGeo("./data/real_test_both.txt",
				"./data/test.txt");

	}
}
