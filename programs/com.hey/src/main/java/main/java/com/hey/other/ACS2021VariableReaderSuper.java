package main.java.com.hey.other;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import main.java.com.hey.Util;

public abstract class ACS2021VariableReaderSuper {

	public static class HtmlData{
		public String name = "";
		public String label = "";
		public String concept = "";
		
		@Override
		public String toString() {
			return "name: " + name + "; label: " + label + "; concept: " + concept;
		}
	}
	
	public List<HtmlData> getHtmlVariables() throws Exception {
		String url = "https://api.census.gov/data/2020/acs/acs5/variables.html";
		Document pod = Jsoup.connect(url).ignoreContentType(true).maxBodySize(0)
			       .timeout(0).get();
		String outerHtml = pod.outerHtml();
		outerHtml = outerHtml.replace("\n", "");
		int startIdx = outerHtml.indexOf("<tbody>") + "<tbody>".length();
		int endIdx = outerHtml.indexOf("</tbody>");
		outerHtml = outerHtml.substring(startIdx, endIdx);
		String[] arr = outerHtml.split("</tr>");
		String st3 = arr[0];
		String lengthyWhiteSpaces = st3.substring(0, st3.indexOf("<tr>")); // whitespace
		List<HtmlData> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].contains("<tr>")) {
				break;
			}
			String st = arr[i].replace(lengthyWhiteSpaces, "");
			st = st.substring(st.indexOf("<td>"));
			int startIdx2 = (st.indexOf("\">"))+2;
			HtmlData data = new HtmlData();
			data.name = st.substring(startIdx2, st.indexOf("</a>"));
			st = st.substring(st.indexOf("</td><td>") + "</td><td>".length());
			data.label = st.substring(0, st.indexOf("</td>")).toLowerCase();
			st = st.substring(st.indexOf("</td><td>") + "</td><td>".length());
			data.concept = st.substring(0, st.indexOf("</td>")).toLowerCase();
			list.add(data);
		}
		return list;
	}
	
	public abstract boolean shouldPrintData(HtmlData data);
	
	public void doEverything() throws Exception {
		List<HtmlData> dataList = getHtmlVariables();
		int numElementsPrinted = 0;
		for (HtmlData data : dataList) {
			if (shouldPrintData(data)) {
				System.out.println(data);	
				numElementsPrinted++;
			}
		}
		System.out.println("printed " + numElementsPrinted + " of " + dataList.size() + " elements");
		
	}
}
