package generateTest;

import java.util.Comparator;

class TestCaseComparator implements Comparator<TestCase> {
    @Override
    public int compare(TestCase a, TestCase b) {
        return a.getFormula().length() < b.getFormula().length() ? -1 : 
        	a.getFormula().length() == b.getFormula().length() ? 0 : 1;
    }
}