package main.java.com.hey.useful.not.needed.to.modify.much;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Pojo {
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
