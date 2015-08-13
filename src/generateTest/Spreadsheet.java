package generateTest;

import java.util.ArrayList;

public class Spreadsheet {
	private ArrayList<String> relations;
	private ArrayList<String> literals;

	

	
	public Spreadsheet(ArrayList<String> literals, ArrayList<String> relations) {
		this.literals = literals;
		this.relations = relations;
	}
	
	public boolean isRelation(String st){
		for(String s : relations){
			if(st.equals(s) || st.equals(s+"s") || st.equals(s+"es"))
				return true;
		}
		return false;
	}
	
	public boolean isLiteral(String st){
		for(String s : literals){
			if(s.equals(st))
				return true;
		}
		return false;
	}
	
	public void print(){
		System.out.println("relations------------------");
		for(String s : relations)
			System.out.println(s);
		System.out.println("literals-------------------");
		for(String s : literals){
			System.out.println(s);
		}
	}
}
