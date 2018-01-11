package com.richard.tdd.services;

import static com.richard.tdd.builders.FilmeBuilder.umFilme;
import static com.richard.tdd.builders.FilmeBuilder.umFilmeSemEstoque;
import static com.richard.tdd.builders.LocacaoBuilder.umLocacao;
import static com.richard.tdd.builders.UsuarioBuilder.umUsuario;
import static com.richard.tdd.matchers.MatchersProprios.ehHoje;
import static com.richard.tdd.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static com.richard.tdd.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.richard.tdd.daos.LocacaoDAO;
import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.matchers.MatchersProprios;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;
import com.richard.tdd.utils.DataUtils;

public class LocacaoServiceTest {
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	private LocacaoService locacaoService;
	private Usuario usuario;
	private List<Filme> filmes;
	
	@Before
	public void setup() {
		//cenario
		locacaoService = new LocacaoService();
		usuario = umUsuario().agora();
		filmes = Arrays.asList(umFilme().agora());
		
		dao = Mockito.mock(LocacaoDAO.class);
		locacaoService.setDao(dao);
		
		spcService = Mockito.mock(SPCService.class);
		locacaoService.setSpcService(spcService);
		
		emailService = Mockito.mock(EmailService.class);
		locacaoService.setEmailService(emailService);
	}
	
	/**
	 * 
	 * @throws FilmeException
	 * @throws LocadoraException
	 */
	@Test
	public void deveAlugarFilme() throws FilmeException, LocadoraException {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario 
		filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
	}
	
	@Test(expected = FilmeException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws FilmeException, LocadoraException {
		//cenario
		filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
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
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeException, LocadoraException {
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Filme vazio");
		
		//acao
		locacaoService.alugarFilme(usuario, null);
	}
	
	//@Ignore
	@Test
	public void deveDevolverNaSegundaAoAlgarNoSabado() throws FilmeException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		//cenario
		Usuario usuario = umUsuario().agora();
		
		//acao 
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
//		boolean ehSegunda  = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
//		assertTrue(ehSegunda);
		
//		assertThat(retorno.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora()) ;
		
		when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		//acao
		try {
			locacaoService.alugarFilme(usuario, filmes);			
		//verificacao
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario negativado !"));
		}
		
		verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Locacao> locacoes = Arrays.asList(umLocacao()
												.comUsuario(usuario)
												.comDataRetorno(obterDataComDiferencaDias(-2))
												.agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtrasos();
		
		//verificacao
		verify(emailService).notificarAtraso(usuario);
		
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
//	public static void main(String[] args) {
//		new BuilderMaster().gerarCodigoClasse(Locacao.class);
//	}
	
}