package com.richard.tdd.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;
import com.richard.tdd.utils.DataUtils;

public class LocacaoServiceTest {
	
	private LocacaoService locacaoService;
	private Usuario usuario;
	private List<Filme> filmes;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		//cenario
		locacaoService = new LocacaoService();
		usuario = new Usuario("Usuario 1");
		filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0),
				               new Filme("Filme 2", 3, 6.0));
		
	}
	
	@Test
	public void testLocacao() throws FilmeException, LocadoraException {
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(11.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	//teste de forma elegante
	@Test(expected = FilmeException.class)
	public void testLocacaoo_filmeSemEstoque() throws FilmeException, LocadoraException {
		filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), new Filme("Filme 2", 2, 5.0));
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	//teste de forma robusta
	@Test
	public void testLocacaoo_usuarioVazio() throws FilmeException {
		//acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	//teste de forma nova
	@Test
	public void testLocacaoo_filmeVazio() throws FilmeException, LocadoraException {
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Filme vazio");
		
		//acao
		locacaoService.alugarFilme(usuario, null);
	}
	
}