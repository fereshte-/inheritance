package generateTest;

public class TestCase {
	private String sentence, formula;
	
	public TestCase(String sentence, String formula) {
		this.sentence = sentence;
		this.formula = formula;
	}
	
	public TestCase() {
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
