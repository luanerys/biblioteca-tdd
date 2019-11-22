package com.br.ed2.tdd.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprestimo {

	private Usuario usuario;
	private final LocalDate dataEmprestimo = LocalDate.now();
	private final LocalDate dataPrevista = dataEmprestimo.plusDays(7);
	private LocalDate dataDevolucao;
	private Livro livro;
	private final double valorFixo = 5.0;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public LocalDate getDataDevolucao() {
		return dataDevolucao;
	}

	public void setDataDevolucao(LocalDate dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public LocalDate getDataEmprestimo() {
		return dataEmprestimo;
	}

	public LocalDate getDataPrevista() {
		return dataPrevista;
	}

	public double getValorFixo() {
		return valorFixo;
	}

	public double valorTotalAPagar() {

		if (this.dataDevolucao.isBefore(this.dataPrevista) || this.dataDevolucao.isEqual(this.dataPrevista)) {

			return this.getValorFixo();

		} else {
			long duracao = ChronoUnit.DAYS.between(this.dataPrevista, this.dataDevolucao);

			if (duracao <= 8)
				return this.getValorFixo() + (duracao * 0.40);
		}

		return this.getValorFixo() + 3.0;

	}

}
