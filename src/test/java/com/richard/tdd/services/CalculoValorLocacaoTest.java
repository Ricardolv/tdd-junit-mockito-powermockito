package com.richard.tdd.services;

import static com.richard.tdd.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.richard.tdd.daos.LocacaoDAO;
import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	private LocacaoService locacaoService;
	private Usuario usuario;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value = 1)
	public Double valorLocacao;
	
	@Parameter(value = 2)
	public String cenario;
	
	@Before
	public void setup() {
		//cenario
		locacaoService = new LocacaoService();
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		locacaoService.setDao(dao);
		usuario = umUsuario().agora();
		SPCService spcService = Mockito.mock(SPCService.class);
		locacaoService.setSpcService(spcService);
		
	}
	
	private static Filme filme1 = new Filme("Filme 1", 2, 4.0);
	private static Filme filme2 = new Filme("Filme 2", 2, 4.0);
	private static Filme filme3 = new Filme("Filme 3", 2, 4.0);
	private static Filme filme4 = new Filme("Filme 4", 2, 4.0);
	private static Filme filme5 = new Filme("Filme 5", 2, 4.0);
	private static Filme filme6 = new Filme("Filme 6", 2, 4.0);
	private static Filme filme7 = new Filme("Filme 7", 2, 4.0);
	
	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] {
			{ Arrays.asList(filme1, filme2), 8.0, "2 filmes sem desconto" },
			{ Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 25%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes: 50%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 filmes: 75%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 filmes: 100%" },
			{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 filmes sem desconto" }
		});
	}
	
	@Test
	public void deveCalcularValorLocacaoConsiderandoDesconto() throws FilmeException, LocadoraException {
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(valorLocacao));
	}

}
