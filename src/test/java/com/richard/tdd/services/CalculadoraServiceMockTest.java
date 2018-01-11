package com.richard.tdd.services;

import org.junit.Test;
import org.mockito.Mockito;

import com.richard.tdd.model.Calculadora;

public class CalculadoraServiceMockTest {
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		Mockito.when(calc.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
		
		System.out.println(calc.somar(1, 3));
	}

}
