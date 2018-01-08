package com.richard.tdd.services;

import static com.richard.tdd.utils.DataUtils.adicionarDias;

import java.util.Date;

import com.richard.tdd.modal.Filme;
import com.richard.tdd.modal.Locacao;
import com.richard.tdd.modal.Usuario;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws Exception {
		
		if (filme.getEstoque() == 0) {
			throw new Exception("Filme sem estoque");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.	setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}

}