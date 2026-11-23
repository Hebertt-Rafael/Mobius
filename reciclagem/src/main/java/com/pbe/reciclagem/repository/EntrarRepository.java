package com.pbe.reciclagem.repository;

import com.pbe.reciclagem.model.Entrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrarRepository extends JpaRepository<Entrar, Long> {

    Optional<Entrar> findByEmail(String email);
}
