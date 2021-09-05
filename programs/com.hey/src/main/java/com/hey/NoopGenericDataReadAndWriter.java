package com.hey;

public class NoopGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(Data data, String stateName) {
		
	}
	
	public static void main(String[] args) throws Exception {

		GenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		n.processAllStates();

	}

}
