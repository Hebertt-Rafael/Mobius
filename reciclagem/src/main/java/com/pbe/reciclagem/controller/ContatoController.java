package com.pbe.reciclagem.controller;

import com.pbe.reciclagem.model.Contato;
import com.pbe.reciclagem.repository.ContatoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contato")
public class ContatoController {

    @Autowired
    private ContatoRepository contatoRepository;

    // ── GET /contato ───────────────────────────────────────────────────────────
    @GetMapping
    public String paginaContato(Model model) {
        model.addAttribute("contato", new Contato());
        return "contato/index";
    }

    // ── POST /contato ──────────────────────────────────────────────────────────
    @PostMapping
    public String enviarContato(
            @Valid @ModelAttribute("contato") Contato contato,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "contato/index";
        }

        contatoRepository.save(contato);

        redirectAttributes.addFlashAttribute("sucesso", true);
        redirectAttributes.addFlashAttribute("assuntoEnviado", contato.getAssunto());
        return "redirect:/contato";
    }
}
