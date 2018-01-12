package com.richard.tdd.model;

public class Calculadora {

	public int somar(int a, int b) {
		System.out.println("Somando !!!");
		return a + b;
	}

	public int subtrair(int a, int b) {
		return a - b;
	}

	public int dividir(int a, int b) {
		return a / b;
	}
	
	public void imprime() {
		System.out.println("Opa !!!");
	}

}
