package main.java.com.hey.other;

public class ACS2021VariableReader extends ACS2021VariableReaderSuper {

	@Override
	public boolean shouldPrintData(HtmlData data) {
		if (data.concept.contains("in puerto rico")) {
			return false;
		}
		return data.concept.contains("children under 18 years") && data.label.contains("householder") && (data.concept.contains("black") || data.label.contains("black"));// && data.concept.contains("black");
	}
	
	public static void main(String[] args) throws Exception {
		new ACS2021VariableReader().doEverything();

	}

}
