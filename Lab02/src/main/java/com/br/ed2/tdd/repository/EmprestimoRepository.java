package com.br.ed2.tdd.repository;

import java.util.List;

import com.br.ed2.tdd.modelo.Emprestimo;
import com.br.ed2.tdd.modelo.Livro;

public interface EmprestimoRepository {

	String salvaEmprestimo(Emprestimo emprestimo);
	
	List<Livro> listaDeLivrosEmAtraso();
	
}
