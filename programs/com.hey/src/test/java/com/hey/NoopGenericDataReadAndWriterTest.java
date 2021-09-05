package com.hey;

import java.io.File;
import java.util.Scanner;

import org.junit.Test;

import com.hey.complete.NoopGenericDataReadAndWriter;

public class NoopGenericDataReadAndWriterTest {

	@Test
	public void hi() throws Exception {
		NoopGenericDataReadAndWriter n = new NoopGenericDataReadAndWriter();
		// cannot do async in junit
	//	n.runStateAsync("Alaska");
	}

}
