package com.hey;

import java.util.List;

public class NoopGenericDataReadAndWriter extends GenericDataReadAndWriter {

	@Override
	protected void updateData(List<Data> dataList) {
		
	}
	
	public static void main(String[] args) throws Exception {

		NoopGenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		n.processAllStates();

	}

}
