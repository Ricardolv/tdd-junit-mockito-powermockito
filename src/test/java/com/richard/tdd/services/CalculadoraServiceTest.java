package com.richard.tdd.services;

import org.junit.Assert;
import org.junit.Test;

import com.richard.tdd.model.Calculadora;

public class CalculadoraServiceTest {
	
	@Test
	public void deveSomarDoisValores() {
		//cenario
		int a = 5;
		int b = 12;
		Calculadora calc = new Calculadora();
		
		//acao
		int resultado = calc.somar(a, b);
		
		//verificacao
		Assert.assertEquals(17, resultado);
		
		
	}
	
	@Test
	public void deveSsubtrairDoisValores() {
		//cenario
		int a = 12;
		int b = 5;
		Calculadora calc = new Calculadora();
		
		//acao
		int resultado = calc.subtrair(a, b);
		
		//verificacao
		Assert.assertEquals(7, resultado);
		
		
	}
	
	@Test
	public void deveDividirDoisValores() {
		//cenario
		int a = 6;
		int b = 2;
		Calculadora calc = new Calculadora();
		
		//acao
		int resultado = calc.dividir(a, b);
		
		//verificacao
		Assert.assertEquals(3, resultado);
	}

}
