package com.pbe.reciclagem.controller;

import com.pbe.reciclagem.model.Pontos;
import com.pbe.reciclagem.repository.PontosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PontosController {

    @Autowired
    private PontosRepository pontosRepository;

    // ── GET /pontos, /pntosdecoleta, /pontosdecoleta ────────────────────────────
    // Renderiza a página do mapa com todos os pontos
    @GetMapping({"/pontos", "/pntosdecoleta", "/pontosdecoleta"})
    public String paginaPontos(Model model) {
        model.addAttribute("pontos", pontosRepository.findAll());
        return "pontosdecoleta/index";
    }

    // ── GET /pontos/api e /api/pontos ─────────────────────────────────────────
    // Retorna JSON para filtragem dinâmica via JavaScript
    @GetMapping({"/pontos/api", "/pontos/api/pontos"})
    @ResponseBody
    public List<Pontos> filtrarPontos(
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro) {

        boolean temUf = StringUtils.hasText(uf) && !uf.equalsIgnoreCase("all");
        boolean temCidade = StringUtils.hasText(cidade) && !cidade.equalsIgnoreCase("all");
        boolean temBairro = StringUtils.hasText(bairro) && !bairro.equalsIgnoreCase("all");

        if (temUf && temCidade && temBairro) {
            return pontosRepository.findByUfAndCidadeAndBairro(uf.toUpperCase(), cidade, bairro);
        } else if (temUf && temCidade) {
            return pontosRepository.findByUfAndCidade(uf.toUpperCase(), cidade);
        } else if (temUf) {
            return pontosRepository.findByUf(uf.toUpperCase());
        }

        return pontosRepository.findAll();
    }
}
