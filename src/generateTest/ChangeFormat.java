package generateTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lambda.Exp;


public class ChangeFormat {
	PrintWriter out, outTest, out1, out2;
	String real_train, real_test;
	double testPercent = 0.7;


	public static boolean isNumeric(String str)  
	{  
		try  
		{  
			Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}

	private void sortFile(String fileName) throws IOException{
		ArrayList<TestCase> list = makeToArray(fileName);
		Collections.sort(list, new TestCaseComparator());
		for(TestCase testCase : list){
			if(testCase.getFormula().length() > 60) continue;
			out.println(testCase.getSentence());
			out.println(testCase.getFormula() + "\n");
		}
		out.close();
	}

	private boolean contain_1(String s, List<String> list){
		for(String t: list){
			if(s.contains(t))
				return true;
		}
		return false;
	}

	private boolean contain_2(String s, List<List<String> > list_list){
		for(List<String> list: list_list){
			if(!contain_1(s, list))
				return false;
		}
		return true;
	}

	private void makePortion(double portion, String inName, String outName) throws IOException{
		ArrayList<TestCase> list = makeToArray(inName);
		PrintWriter outFile = new PrintWriter(outName, "UTF-8");
		int size = list.size();
		//	Collections.shuffle(list);//, new TestCaseComparator());
		//	list.subList((int) (portion * size), size).clear();
		ArrayList<TestCase> list2 = new ArrayList<TestCase>();

		for(TestCase testCase : list){
			//	if(
			//		(!testCase.getFormula().contains("arg")) ||
			//	testCase.getSentence().contains("river") || 
			//testCase.getFormula().contains("river") ||
			//				 testCase.getFormula().length() >  80
			//				)  continue;
			list2.add(testCase);
		}

		int size2 = list2.size();
		list2.subList((int) (portion * size2), size2).clear();
		for(TestCase testCase : list2){
			outFile.println(testCase.getSentence());
			outFile.println(testCase.getFormula() + "\n");
		}
		outFile.close();


	}

	private void readyForGiza(String fileName) throws IOException{
		ArrayList<TestCase> list = makeToArray(fileName);
		for(TestCase testCase : list){
			out1.println(testCase.getSentence());
			String formul = testCase.getFormula();
			formul = formul.replaceAll("[()$]|lambda| e ", " ");
			out2.println(formul);
		}
		out1.close();
		out2.close();
	}

	private ArrayList<TestCase> makeToArray(String fileName) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ArrayList<TestCase> list = new ArrayList<TestCase>();
		String line = "";
		while (line!=null){
			// read sentence
			line = "";
			while (line!=null && (line.equals("") || line.startsWith("//"))){
				line = in.readLine();
				if (line!=null)
					line = line.trim();
			}
			TestCase testCase = new TestCase();
			if (line!=null && !line.equals("") && !line.startsWith("//")){
				testCase.setSentence(line);
				// read semantics
				line = in.readLine();
				if (line!=null){
					line.trim();				
					testCase.setFormula(line);
					list.add(testCase);

				}
			}
		}

		return list;
	}


	public void change(String fileName, String formula, Spreadsheet sp) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		ArrayList<String> list = new ArrayList<String>();
		String line = "";
		while((line=in.readLine()) != null){
			String  changedLine = "";
			String[] tmp = line.split("\\s+");
			for(String s:tmp){
				String tmp2=s.replace("$", " ").trim();
				if(tmp2.matches("\\d+"))
					s="CONST";
				else if(sp.isLiteral(s))
					s="lit";
				else if(sp.isRelation(s)){
					s="rel";
				}

				changedLine = changedLine + s + " ";
			}
			changedLine= changedLine +  "\n";
			for(String s:formula.split("\\s+")){
				tmp = s.split(":");
				String tmp2=tmp[0].replace("$", " ").trim();
				if(tmp2.matches("\\d+"))
					tmp[0]="CONST";
				else if(sp.isLiteral(tmp[0]))
					tmp[0]="lit";
				else if(sp.isRelation(tmp[0])){
					tmp[0]="rel";
				}
				if(tmp.length > 1)
					s = tmp[0] + ":" + tmp[1];

				changedLine = changedLine + s + " ";
			}
			changedLine= changedLine +  "\n\n";

			list.add(changedLine);
		}
		for(int i=0; i<list.size()*testPercent; i++)
			out.write(list.get(i));
		for(int i=(int) (list.size()*testPercent); i<list.size(); i++){
			outTest.write(list.get(i));
		}
	}


	public static void main(String[] args) throws IOException {
		ChangeFormat changeFormat = new ChangeFormat();
		changeFormat.out = new PrintWriter("geo880-funql.test", "UTF-8");
		changeFormat.outTest = new PrintWriter("geo880-funql.test", "UTF-8");
		changeFormat.out1 = new PrintWriter("b.txt", "UTF-8");
		changeFormat.out2 = new PrintWriter("a.txt", "UTF-8");



		if(isNumeric(args[0])){
			System.out.println("hello!!!!!!!");

			double portion = Double.parseDouble(args[0]) / 10 ;
			changeFormat.makePortion(portion, "../../data/train.txt", "train.txt");
			changeFormat.makePortion(0.5, "../../data/test.txt", "test.txt");
		}else{
			String fileName = args[0];
			changeFormat.readyForGiza(fileName);
		}


	}
}
