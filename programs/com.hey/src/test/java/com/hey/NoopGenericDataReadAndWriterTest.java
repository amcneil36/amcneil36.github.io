package com.hey;

import java.io.File;
import java.util.Scanner;

import org.junit.Test;

public class NoopGenericDataReadAndWriterTest {

	@Test
	public void hi() throws Exception {
		NoopGenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		n.runStateSync("Delaware");
	}
	
	//@Test
	public void hah() throws Exception{
		String stateName = "Delaware";
		String filePath = "C:\\Users\\anmcneil\\amcneil36.github.io\\programs\\cityLookup\\States\\" + stateName
				+ ".js";
		System.out.println(filePath);
		File myObj = new File(filePath);
		Scanner myReader = new Scanner(myObj);
		System.out.println("sdalkfj");
	}
}
