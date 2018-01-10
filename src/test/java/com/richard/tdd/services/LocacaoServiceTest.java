package com.richard.tdd.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
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
	
	/**
	 * 
	 * @throws FilmeException
	 * @throws LocadoraException
	 */
	@Test
	public void deveAlugarFilme() throws FilmeException, LocadoraException {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(11.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}
	
	/**
	 * Teste de forma elegante
	 * @throws FilmeException
	 * @throws LocadoraException
	 */
	@Test(expected = FilmeException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws FilmeException, LocadoraException {
		filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0), new Filme("Filme 2", 2, 5.0));
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	/**
	 * Teste de forma robusta
	 * @throws FilmeException
	 */
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeException {
		//acao
		try {
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
    /**
     * Teste de forma nova
     * @throws FilmeException
     * @throws LocadoraException
     */
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeException, LocadoraException {
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Filme vazio");
		
		//acao
		locacaoService.alugarFilme(usuario, null);
	}
	
	/**
	 * Deve pagar 75% no fime 3
	 * @throws LocadoraException 
	 * @throws FilmeException 
	 */
	@Test
	public void devePagar75PctNoFilme3() throws FilmeException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										  new Filme("Filme 2", 2, 4.0), 
										  new Filme("Filme 3", 2, 4.0));
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(11.0));
	}
	
	/**
	 * Deve pagar 50% no fime 4
	 * @throws LocadoraException 
	 * @throws FilmeException 
	 */
	@Test
	public void devePagar50PctNoFilme4() throws FilmeException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										  new Filme("Filme 2", 2, 4.0), 
										  new Filme("Filme 3", 2, 4.0), 
										  new Filme("Filme 4", 2, 4.0));
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(13.0));
	}
	
	/**
	 * Deve pagar 25% no fime 5
	 * @throws LocadoraException 
	 * @throws FilmeException 
	 */
	@Test
	public void devePagar25PctNoFilme5() throws FilmeException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										  new Filme("Filme 2", 2, 4.0), 
										  new Filme("Filme 3", 2, 4.0), 
										  new Filme("Filme 4", 2, 4.0),
										  new Filme("Filme 5", 2, 4.0));
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
	}
	
	/**
	 * Deve pagar 0% no fime 6
	 * @throws LocadoraException 
	 * @throws FilmeException 
	 */
	@Test
	public void devePagar0PctNoFilme6() throws FilmeException, LocadoraException {
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0), 
										  new Filme("Filme 2", 2, 4.0), 
										  new Filme("Filme 3", 2, 4.0), 
										  new Filme("Filme 4", 2, 4.0),
										  new Filme("Filme 5", 2, 4.0),
										  new Filme("Filme 6", 2, 4.0));
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
	}
	
	//@Ignore
	@Test
	public void deveDevolverNaSegundaAoAlgarNoSabado() throws FilmeException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 5.0));
		
		//acao 
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		boolean ehSegunda  = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		assertTrue(ehSegunda);
	}
	
}