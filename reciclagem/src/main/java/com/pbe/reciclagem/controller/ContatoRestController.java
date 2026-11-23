package com.pbe.reciclagem.controller;

import com.pbe.reciclagem.model.Contato;
import com.pbe.reciclagem.repository.ContatoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contato")
public class ContatoRestController {

    @Autowired
    private ContatoRepository contatoRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> salvarContato(
            @Valid @RequestBody Contato contato,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            String primeiroErro = result.getAllErrors().getFirst().getDefaultMessage();
            response.put("ok", false);
            response.put("erro", primeiroErro != null ? primeiroErro : "Dados inválidos.");
            return ResponseEntity.badRequest().body(response);
        }

        if (!StringUtils.hasText(contato.getNome()) ||
            !StringUtils.hasText(contato.getEmail()) ||
            !StringUtils.hasText(contato.getCidade()) ||
            !StringUtils.hasText(contato.getMensagem())) {
            response.put("ok", false);
            response.put("erro", "Preencha todos os campos obrigatórios (*).");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            contatoRepository.save(contato);
            response.put("ok", true);
            response.put("mensagem", "Mensagem enviada com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("ok", false);
            response.put("erro", "Erro ao salvar mensagem no servidor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
