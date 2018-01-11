package com.richard.tdd.builders;

import com.richard.tdd.model.Usuario;

public class UsuarioBuilder {
	
	private Usuario usuario;

	private UsuarioBuilder() {
	}
	
	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario("Usuario 1");
		return builder;
	}
	
	public UsuarioBuilder comNome(String nome) {
		usuario.setNome(nome);
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}
	
	

}
