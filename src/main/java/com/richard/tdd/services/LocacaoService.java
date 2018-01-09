package com.richard.tdd.services;

import static com.richard.tdd.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeException, LocadoraException  {
		
		validarUsuario(usuario);
		validarEstoque(filmes);
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.	setDataLocacao(new Date());
		double valores = filmes.stream().collect(Collectors.summingDouble(f -> f.getPrecoLocacao()));	
				
		locacao.setValor(valores);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}

	private void validarUsuario(Usuario usuario) throws LocadoraException {
		if (null == usuario)
			throw new LocadoraException("Usuario vazio");
	}

	private void validarEstoque(List<Filme> filmes) throws FilmeException, LocadoraException {
		
		if (null == filmes || filmes.isEmpty())
			throw new LocadoraException("Filme vazio");
		
		boolean algumFilmeSemEstoque =  filmes.stream().anyMatch(new Predicate<Filme>() {
			public boolean test(Filme input) {
				return input.getEstoque() == 0;
			}
		});
		
		if (algumFilmeSemEstoque)
			throw new FilmeException();
	}

}