package com.hey;

import java.util.List;

public class NoopGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(List<Data> dataList, String stateName) {
		
	}
	
	public static void main(String[] args) throws Exception {

		GenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		n.processAllStates();

	}

}
