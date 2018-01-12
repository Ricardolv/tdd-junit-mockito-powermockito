package com.richard.tdd.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.richard.tdd.model.Calculadora;
import com.richard.tdd.model.Locacao;


public class CalculadoraServiceMockTest {
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		assertEquals(5, calc.somar(1, 3));
		System.out.println(argCapt.getAllValues());
	}

}
