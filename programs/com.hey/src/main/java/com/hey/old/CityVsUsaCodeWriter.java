package main.java.com.hey.old;

import java.util.ArrayList;
import java.util.List;

import main.java.com.hey.Util;

public class CityVsUsaCodeWriter {
	
	public enum Type {
	    FLOAT_PERCENT {
			@Override
			String getSmallType() {
				// TODO Auto-generated method stub
				return "float";
			}

			@Override
			String getBigType() {
				// TODO Auto-generated method stub
				return "Float";
			}

			@Override
			String getReadParsePrefix() {
				// TODO Auto-generated method stub
				return "getValidFloatFromPercent";
			}

			@Override
			String findMeanText() {
				// TODO Auto-generated method stub
				return "findMeanFloat";
			}

			@Override
			String findMedianText() {
				// TODO Auto-generated method stub
				return "findMedianFloat";
			}

			@Override
			String getSbAppendPrefix() {
				// TODO Auto-generated method stub
				return "getFloatString";
			}

			@Override
			String getMetricText() {
				// TODO Auto-generated method stub
				return "getMetricFloat";
			}

			@Override
			String getAppendMetricText() {
				return "appendFloatMetric";
			}
		};
	    
	    abstract String getSmallType();
	    abstract String getBigType();
	    abstract String getReadParsePrefix();
	    abstract String findMeanText();
	    abstract String findMedianText();
	    abstract String getSbAppendPrefix();
	    abstract String getMetricText();
	    abstract String getAppendMetricText();
	}
	
	private static final String VARIABLE_NAME = "povertyRate";
	private static final Type TYPE = Type.FLOAT_PERCENT; 

	public static void main(String[] args) throws Exception {
		String inputFilepath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\src\\main\\java\\com\\hey\\old\\CityVsUSAComparison2.java";
		List<String> inputText = Util.readTextFromFile(inputFilepath);
		StringBuilder sb = new StringBuilder();
		String tempText = "";
		for (String st : inputText) {
			if (st.contains("TODO1") && !st.contains("TODO10") && !st.contains("TODO11")){
				if (TYPE == Type.FLOAT_PERCENT) {
					sb.append("		").append(TYPE.getSmallType()).append(" " + VARIABLE_NAME + ";\n");
				}
			}
			else if(st.contains("TODO2")) {
				sb.append("	static Foo<").append(TYPE.getBigType()).append("> ");
				sb.append(VARIABLE_NAME).append("Foo = new Foo<").append(TYPE.getBigType());
				sb.append(">() { @Override ").append(TYPE.getBigType()).append(" getData(InputData inputData) { return inputData.");
				sb.append(VARIABLE_NAME).append(";}};\n");
			}
			else if(st.contains("TODO3")) {
				sb.append("		").append(TYPE.getSmallType()).append(" ").append(VARIABLE_NAME).append("Average;\n");
				sb.append("		").append(TYPE.getSmallType()).append(" ").append(VARIABLE_NAME).append("Median;\n");
			}
			else if(st.contains("TODO4")) {
				sb.append("		public Metric ").append(VARIABLE_NAME).append("Metric = new Metric();\n");
			}
			else if(st.contains("TODO5")) {
				sb.append("				inputData.").append(VARIABLE_NAME).append(" = ");
				sb.append(TYPE.getReadParsePrefix());
				sb.append("(data.").append(VARIABLE_NAME).append(");\n");
			}
			else if(st.contains("TODO6")) {
				sb.append("		List<").append(TYPE.getBigType()).append("> ").append(VARIABLE_NAME).append("List = ").append(VARIABLE_NAME).append("Foo.getGenericList(inputDataList);\n");
				sb.append("		obj.").append(VARIABLE_NAME).append("Average = ").append(TYPE.findMeanText()).append("(").append(VARIABLE_NAME).append("List);\n");
				sb.append("		obj.").append(VARIABLE_NAME).append("Median = ").append(TYPE.findMedianText()).append("(").append(VARIABLE_NAME).append("List);\n");
			}
			else if(st.contains("TODO7")) {
				sb.append("		sb.append(").append(TYPE.getSbAppendPrefix()).append("(\"").append(VARIABLE_NAME).append("Average\", averagesAndMedians.").append(VARIABLE_NAME).append("Average));\n");
				sb.append("		sb.append(").append(TYPE.getSbAppendPrefix()).append("(\"").append(VARIABLE_NAME).append("Median\", averagesAndMedians.").append(VARIABLE_NAME).append("Median));\n");
			}
			else if (st.contains("TODO8")) {
				sb.append("			outputData.").append(VARIABLE_NAME).append("Metric = ").append(VARIABLE_NAME).append("Foo.").append(TYPE.getMetricText()).append("(inputData.").append(VARIABLE_NAME).append(", inputDataList);\n");
			}
			else if (st.contains("TODO9")) {
				sb.append("		sb.append(\"var ").append(VARIABLE_NAME).append("Metric;\\n\");)\n");
			}
			else if (st.contains("TODO10")) {
				sb.append("			").append(TYPE.getAppendMetricText()).append("(sb, outputData.").append(VARIABLE_NAME).append("Metric, \"").append(VARIABLE_NAME).append("Metric\");\n");
			}
			else if (st.contains("sb.append(\"cityData = new CityData(populationMetri")) {
				tempText = st;
				continue;
			}
			else if (st.contains("TODO11")) {
				tempText = tempText.substring(0, tempText.length()-3);
				tempText += ", " + VARIABLE_NAME + "Metric\");";
				sb.append(tempText).append("\n");
			}
			sb.append(st);
			sb.append("\n");
		}
		String outputFilepath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\com.hey\\generatedCode.txt";
		Util.writeTextToFile(outputFilepath, sb.toString());
		System.out.println("wrote to file");
	}

}
