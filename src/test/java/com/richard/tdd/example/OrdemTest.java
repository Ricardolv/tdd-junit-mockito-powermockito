package com.richard.tdd.example;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTest {
	
	private static int count = 0;
	
	@Test
	public void init() {
		count = 1;
	}
	
	@Test
	public void verification() {
		Assert.assertEquals(1, count);
	}

}
