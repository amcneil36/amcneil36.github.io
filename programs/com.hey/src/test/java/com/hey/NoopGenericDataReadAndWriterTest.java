package com.hey;

import java.io.File;
import java.util.Scanner;

import org.junit.Test;

public class NoopGenericDataReadAndWriterTest {

	@Test
	public void hi() throws Exception {
		NoopGenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		n.runStateAsync("Alaska");
	}

}
