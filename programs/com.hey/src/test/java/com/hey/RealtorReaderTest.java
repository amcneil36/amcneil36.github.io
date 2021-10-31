package com.hey;

import org.junit.Test;

import com.hey.old.RealtorCountyReader;

import org.junit.Assert;

public class RealtorReaderTest {

	@Test
	public void hi() {
		Assert.assertEquals(259900, RealtorCountyReader.getIntFromString("$259.9K"));
	}
	
	@Test
	public void hi2() {
		Assert.assertEquals(1500000, RealtorCountyReader.getIntFromString("$1.5M"));
	}
	
	@Test
	public void hi3() {
		Assert.assertEquals(1000000, RealtorCountyReader.getIntFromString("$1M"));
	}
	
	@Test
	public void hi4() {
		Assert.assertEquals(259000, RealtorCountyReader.getIntFromString("$259K"));
	}
	
	@Test
	public void hi5() {
		Assert.assertEquals(125, RealtorCountyReader.getIntFromString("$125"));
	}
}
