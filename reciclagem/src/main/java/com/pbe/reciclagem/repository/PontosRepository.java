package com.pbe.reciclagem.repository;

import com.pbe.reciclagem.model.Pontos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontosRepository extends JpaRepository<Pontos, Long> {

    List<Pontos> findByUf(String uf);

    List<Pontos> findByUfAndCidade(String uf, String cidade);

    List<Pontos> findByUfAndCidadeAndBairro(String uf, String cidade, String bairro);
}
