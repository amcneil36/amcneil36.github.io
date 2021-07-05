package com.hey;

import org.junit.Test;

import org.junit.Assert;

public class RealtorReaderTest {

	@Test
	public void hi() {
		Assert.assertEquals(259900, RealtorReader.getIntFromString("$259.9K"));
	}
	
	@Test
	public void hi2() {
		Assert.assertEquals(1500000, RealtorReader.getIntFromString("$1.5M"));
	}
	
	@Test
	public void hi3() {
		Assert.assertEquals(1000000, RealtorReader.getIntFromString("$1M"));
	}
	
	@Test
	public void hi4() {
		Assert.assertEquals(259000, RealtorReader.getIntFromString("$259K"));
	}
	
	@Test
	public void hi5() {
		Assert.assertEquals(125, RealtorReader.getIntFromString("$125"));
	}
}
