package com.richard.tdd.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.richard.tdd.services.CalculadoraServiceTest;
import com.richard.tdd.services.CalculoValorLocacaoTest;
import com.richard.tdd.services.LocacaoServiceTest;

//@RunWith(Suite.class)
@SuiteClasses({
	CalculadoraServiceTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao {
	
}
