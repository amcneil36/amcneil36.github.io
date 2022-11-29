package main.java.com.hey.other;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Pojo {
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
