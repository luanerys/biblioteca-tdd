package com.br.es2.tdd.builder;

import com.br.ed2.tdd.modelo.Livro;

public class LivroBuilder {
	
	private Livro livro;
	
	private LivroBuilder() {}
	
	public static LivroBuilder umLivro() {
		
		LivroBuilder builder = new LivroBuilder();
		
		builder.livro = new Livro();
		return builder;
	}
	
	
	public LivroBuilder comTitulo(String titulo) {
		this.livro.setTitulo(titulo);
		return this;
	}
	
	public LivroBuilder comAutor(String autor) {
		this.livro.setAutor(autor);
		return this;
	}
	
	public LivroBuilder ehReservado(Boolean valor) {
		this.livro.setReservado(valor);
		return this;
	}
	
	public Livro constroi() {
		return this.livro;
	}

}
