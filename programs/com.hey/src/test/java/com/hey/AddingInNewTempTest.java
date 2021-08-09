package com.hey;

import org.junit.Test;

import org.junit.*;

public class AddingInNewTempTest {

	@Test
	public void negativeElevation() {
		String cityName = "Calipatria";
		String stateName = "California";
		String actual = AddingInNewTemp.getStringToAddToLine(cityName, stateName);
		String expected = "\"Pacific Standard Time\", -45";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void positiveElevation() {
		String cityName = "Oceanside";
		String stateName = "California";
		String actual = AddingInNewTemp.getStringToAddToLine(cityName, stateName);
		String expected = "\"Pacific Standard Time\", 28";
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void elkHart() {
		String cityName = "elkhart";
		String stateName = "indiana";
		String actual = AddingInNewTemp.getStringToAddToLine(cityName, stateName);
		String expected = "\"Eastern Standard Time\", 700";
		Assert.assertEquals(expected, actual);
	}
}
