package com.richard.tdd.services;

import static com.richard.tdd.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.richard.tdd.daos.LocacaoDAO;
import com.richard.tdd.exceptions.FilmeException;
import com.richard.tdd.exceptions.LocadoraException;
import com.richard.tdd.model.Filme;
import com.richard.tdd.model.Locacao;
import com.richard.tdd.model.Usuario;
import com.richard.tdd.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeException, LocadoraException  {
		boolean negativado;
		
		validarUsuario(usuario);
		validarEstoque(filmes);
		
		try {
			negativado = spcService.possuiNegativacao(usuario);
			
		} catch (Exception e) {
			throw new LocadoraException("Problema com SPC, tente novamente");
		}
		
		if (negativado) {
			throw new LocadoraException("Usuario negativado !");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.	setDataLocacao(obterData());
		
//		locacao.setValor(filmes.stream().collect(Collectors.summingDouble(f -> f.getPrecoLocacao())));	
		locacao.setValor(calcularValorLocacao(filmes));

		//Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	private Double calcularValorLocacao(List<Filme> filmes) {
		Double valores = 0d;
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			double valorFilme = filme.getPrecoLocacao(); 
			
			switch (i) {
				case 2: valorFilme = valorFilme * 0.75; break;
				case 3: valorFilme = valorFilme * 0.50; break;
				case 4: valorFilme = valorFilme * 0.25; break;
				case 5: valorFilme =  0d; break;
			}
			
			valores += valorFilme;
		}
		return valores;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		
		locacoes.stream()
					.filter(locacao -> locacao.getDataRetorno().before(obterData()))
					.forEach(locacao -> {
						if (locacao.getDataRetorno().before(obterData()))
							emailService.notificarAtraso(locacao.getUsuario());
					});

	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);
	}
	
	public Double aplicarRegraDeDesconto(Filme filme, List<Filme> filmes) {
		
		double valorFilme = filme.getPrecoLocacao(); 
		if (null != filmes.get(2) && filme.equals(filmes.get(2))) {
			valorFilme = valorFilme * 0.75;
		} 
		return valorFilme;
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
	
	protected Date obterData() {
		return new Date();
	}

	public void setDao(LocacaoDAO dao) {
		this.dao = dao;
	}

	public void setSpcService(SPCService spcService) {
		this.spcService = spcService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
}