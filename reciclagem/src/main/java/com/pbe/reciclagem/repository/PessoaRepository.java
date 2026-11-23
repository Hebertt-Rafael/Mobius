package com.pbe.reciclagem.repository;

import com.pbe.reciclagem.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}