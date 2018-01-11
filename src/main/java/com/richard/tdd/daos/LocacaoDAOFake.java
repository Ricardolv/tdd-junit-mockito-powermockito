package com.richard.tdd.daos;

import java.util.List;

import com.richard.tdd.model.Locacao;

public class LocacaoDAOFake implements LocacaoDAO {

	@Override
	public void salvar(Locacao locacao) {

	}

	@Override
	public List<Locacao> obterLocacoesPendentes() {
		return null;
	}

}
