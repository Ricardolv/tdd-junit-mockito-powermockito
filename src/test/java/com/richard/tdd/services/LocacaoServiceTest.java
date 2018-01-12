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
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.richard.tdd.daos.LocacaoDAO;
import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.matchers.MatchersProprios;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;
import com.richard.tdd.utils.DataUtils;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class, DataUtils.class })
public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService;

	private List<Filme> filmes;
	private Usuario usuario;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		//cenario
		MockitoAnnotations.initMocks(this);
		filmes = Arrays.asList(umFilme().agora());
		usuario = umUsuario().agora();
		locacaoService = PowerMockito.spy(locacaoService);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		//Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario 
		filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(12, 1, 2018));
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(12, 1, 2018)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(13, 1, 2018)), is(true));
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
	public void deveDevolverNaSegundaAoAlgarNoSabado() throws Exception {
		//Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		//cenario
		filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(13, 01, 2018));
		
		//acao 
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		
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
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha inesperada"));
		
		//verificacao
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Problema com SPC, tente novamente");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		int tresDias = 3;
		Locacao locacao = umLocacao().agora();
		
		//acao
		locacaoService.prorrogarLocacao(locacao, tresDias);

		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(tresDias));

	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//cenario 
		
		doReturn(1.0).when(locacaoService, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(locacao.getValor(), is(1.0));
		verifyPrivate(locacaoService).invoke("calcularValorLocacao", filmes);
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		
		//acao
		Double valor = (Double) Whitebox.invokeMethod(locacaoService, "calcularValorLocacao", filmes);
		
		//verificacao
		assertThat(valor , is(4.0));
	}

}