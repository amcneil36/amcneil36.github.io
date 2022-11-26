package main.java.com.hey.other;

public class ACS2021VariableReader extends ACS2021VariableReaderSuper {

	@Override
	public boolean shouldPrintData(HtmlData data) {
		if (data.concept.contains("in puerto rico") || data.concept.contains("geographical") || data.concept.contains("under")) {
			return false;
		}
		return data.concept.contains("foreign") && data.name.contains("001E");
	}
	
	public static void main(String[] args) throws Exception {
		new ACS2021VariableReader().doEverything();

	}

}
