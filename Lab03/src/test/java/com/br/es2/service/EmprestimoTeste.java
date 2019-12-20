package com.br.es2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.br.es2.builder.EmprestimoBuilder;
import com.br.es2.builder.UsuarioBuilder;
import com.br.es2.builder.LivroBuilder;


import br.com.es2.model.Emprestimo;
import br.com.es2.model.Livro;
import br.com.es2.model.Usuario;
import br.com.es2.repository.EmprestimoRepository;
import br.com.es2.service.EmailService;
import br.com.es2.service.EmprestimoService;

public class EmprestimoTeste {

	private EmprestimoService servico;
	private EmprestimoRepository repositorio;
	private EmailService emailService;
	private Usuario luana;
	private Livro livro1, livro2, livro3;

	@BeforeEach
	public void setUp() {

		luana = UsuarioBuilder.umUsuario().comNome("Luana").comMatricula("123").constroi();
		livro1 = LivroBuilder.umLivro().comAutor("Autor 1").comTitulo("Livro 1").ehReservado(false).constroi();
		livro2 = LivroBuilder.umLivro().comAutor("Autor 2").comTitulo("Livro 2").ehReservado(false).constroi();
		livro3 = LivroBuilder.umLivro().comAutor("Autor 3").comTitulo("Livro 3").ehReservado(false).constroi();
		
		repositorio = Mockito.mock(EmprestimoRepository.class);
		emailService = Mockito.mock(EmailService.class);
		servico = new EmprestimoService();
		servico.setRepositorio(repositorio);
		servico.setEmailService(emailService);

	}

	@Test
	public void deveRealizarEmprestimoComLivroNaoReservado() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);
		
		Mockito.verify(repositorio,  Mockito.times(1)).salva(emprestimosRealizados.get(0));

		assertEquals(1, emprestimosRealizados.size());

	}

	@Test
	public void testarEmprestimoComLivroReservado() {

		
		livro1.setReservado(true);

		 assertThrows(RuntimeException.class,
				 	  () -> servico.realizarEmprestimo(luana, livro1),
				 	 "Deveria ter lançado um IllegalArgumentException");

	}

	@Test
	public void deveVerificarDataPrevistaCorretamente() {
		LocalDate dataPrevista = null;

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);

		Mockito.verify(repositorio, Mockito.times(1)).salva(emprestimosRealizados.get(0));

		dataPrevista = emprestimosRealizados.get(0).getDataPrevista();

		assertEquals(LocalDate.now().plusDays(7), dataPrevista);

	}

	@Test
	public void testarUsuarioSemEmprestimo() {

		Usuario ana = UsuarioBuilder.umUsuario().comMatricula("12345").comNome("Andreia").constroi();

		int totalEmprestimo = servico.buscarEmprestimosDo(ana);

		assertEquals(0, totalEmprestimo);
	}

	@Test
	public void testarUsuarioComUmEmprestimo() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);
		
		Mockito.verify(repositorio,  Mockito.times(1)).salva(emprestimosRealizados.get(0));

		int totalEmprestimo = servico.buscarEmprestimosDo(luana);
		assertEquals(1, totalEmprestimo);
	}

	@Test
	public void testarUsuarioComDoisEmprestimo() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1, livro2);
		
		for(int i = 0; i < emprestimosRealizados.size(); i++)
			Mockito.verify(repositorio,  Mockito.times(2)).salva(emprestimosRealizados.get(i));

		int totalEmprestimo = servico.buscarEmprestimosDo(luana);
		assertEquals(2, totalEmprestimo);
	}

	@Test
	public void tentativaDeUsuarioComTresEmprestimo() {

		
		
RuntimeException e = 
				assertThrows(RuntimeException.class,
					 () ->{ 
						servico.realizarEmprestimo(luana, livro1);
						servico.realizarEmprestimo(luana, livro2);
						servico.realizarEmprestimo(luana, livro3);
						},
					 "Deveria ter lancado um IllegalArgumentException");

		assertEquals("Usuario nao pode ter mais de 3 emprestimos em aberto", e.getMessage());

	}

	@Test
	public void devolucaoDeEmprestimoAntesDaDataPrevista() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);

		double total = 0.0;

		total += servico.realizaDevolucao(emprestimosRealizados.get(0), LocalDate.now());

		assertEquals(5.0, total, 0.00001);
		assertFalse(livro1.isEmprestado());

		Mockito.verify(repositorio, Mockito.times(1)).atualiza(emprestimosRealizados.get(0));

	}

	@Test
	public void devolucaoDeEmprestimoNaDaDataPrevista() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);

		double total = 0.0;

		total += servico.realizaDevolucao(emprestimosRealizados.get(0), emprestimosRealizados.get(0).getDataPrevista());

		assertEquals(5.0, total, 0.00001);
		assertFalse(livro1.isEmprestado());

		Mockito.verify(repositorio, Mockito.times(1)).atualiza(emprestimosRealizados.get(0));

	}

	@Test
	public void devolucaoDeEmprestimoUmDiaDaDataPrevista() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);

		double total = 0.0;

		total += servico.realizaDevolucao(emprestimosRealizados.get(0),
				emprestimosRealizados.get(0).getDataPrevista().plusDays(1));

		assertEquals(5.4, total, 0.00001);
		assertFalse(livro1.isEmprestado());

		Mockito.verify(repositorio, Mockito.times(1)).atualiza(emprestimosRealizados.get(0));

	}

	@Test
	public void devolucaoDeEmprestimoComTrintaDiasAposDataPrevista() {

		List<Emprestimo> emprestimosRealizados = servico.realizarEmprestimo(luana, livro1);

		double total = 0.0;

		total += servico.realizaDevolucao(emprestimosRealizados.get(0),
				emprestimosRealizados.get(0).getDataPrevista().plusDays(30));

		assertEquals(8.0, total, 0.00001);
		assertFalse(livro1.isEmprestado());

		Mockito.verify(repositorio, Mockito.times(1)).atualiza(emprestimosRealizados.get(0));

	}
	
	@Test
	public void deveEnviarEmailParaUsuariosAtrasados() {
	
		Usuario usuario1 = UsuarioBuilder.umUsuario().comNome("User1").constroi();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("User2").constroi();
		
		
		
		List<Emprestimo> emprestimosEmAtraso = Arrays.asList(
				EmprestimoBuilder.umEmprestimo().comUsuario(usuario1).comLivro(livro1).emAtraso().constroi(),
				EmprestimoBuilder.umEmprestimo().comUsuario(usuario2).comLivro(livro1).emAtraso().constroi() );
		
		
	
		Mockito.when(repositorio.emprestimosEmAtraso()).thenReturn(emprestimosEmAtraso);
		
		servico.notificaUsuariosEmAtraso();
		
		Mockito.verify(emailService, Mockito.times(2)).notifica(usuario1);
		Mockito.verify(emailService, Mockito.times(2)).notifica(usuario2);
		
		Mockito.verifyNoMoreInteractions(emailService);
		
	}

}
