package com.richard.tdd.daos;

import java.util.List;

import com.richard.tdd.model.Locacao;

public interface LocacaoDAO {
	
	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}
