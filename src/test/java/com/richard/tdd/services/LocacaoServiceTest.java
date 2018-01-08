package com.richard.tdd.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.richard.tdd.modal.Filme;
import com.richard.tdd.modal.Locacao;
import com.richard.tdd.modal.Usuario;
import com.richard.tdd.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testLocacao() throws Exception {
		//cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	//teste elegante
	@Test(expected = Exception.class)
	public void testLocacaoo_filmeSemEstoque() throws Exception {
		//cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		//acao
		locacaoService.alugarFilme(usuario, filme);
	}
	
	//teste robusta
	@Test
	public void testLocacaoo_filmeSemEstoque_2() {
		//cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		//acao
		try {
			locacaoService.alugarFilme(usuario, filme);
			Assert.fail("Deveria ter lancado excecao");
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	//teste elegante
	@Test
	public void testLocacaoo_filmeSemEstoque_3() throws Exception {
		//cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		
		expectedException.expect(Exception.class);
		expectedException.expectMessage("Filme sem estoque");
		
		//acao
		locacaoService.alugarFilme(usuario, filme);
	}
	
}