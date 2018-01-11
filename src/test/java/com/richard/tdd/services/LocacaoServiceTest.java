package com.richard.tdd.services;

import static com.richard.tdd.builders.FilmeBuilder.umFilme;
import static com.richard.tdd.builders.FilmeBuilder.umFilmeSemEstoque;
import static com.richard.tdd.builders.LocacaoBuilder.umLocacao;
import static com.richard.tdd.builders.UsuarioBuilder.umUsuario;
import static com.richard.tdd.matchers.MatchersProprios.ehHoje;
import static com.richard.tdd.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.richard.tdd.daos.LocacaoDAO;
import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.matchers.MatchersProprios;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;
import com.richard.tdd.utils.DataUtils;

public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService;
	@Mock
	private Usuario usuario;
	@Mock
	private List<Filme> filmes;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		//cenario
		MockitoAnnotations.initMocks(this);
	}
	
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
		
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora()) ;
		
		when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
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
		Usuario usuario2 = umUsuario().comNome("Usuario 2").agora();
		Usuario usuario3 = umUsuario().comNome("Usuario 3").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().comUsuario(usuario).atrasado().agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().comUsuario(usuario3).atrasado().agora(),
				umLocacao().comUsuario(usuario3).atrasado().agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtrasos();
		
		//verificacao
		
		//verifica se foi enviado noticacoes por 3 vezes usandos instancia usuario 
		verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		
		//deve mandar email
		verify(emailService).notificarAtraso(usuario);
		
		//verifica se pelo meno uma notificacao foi enviado
		verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		
		//nao deve receber email
		verify(emailService, never()).notificarAtraso(usuario2);
		
		//Nada mais deveria acontecer
		verifyNoMoreInteractions(emailService);
	}
	
	@Test
	public void deveTratarErrorNoSpc() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha inesperada"));
		
		//verificacao
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Problema com SPC, tente novamente");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}

}