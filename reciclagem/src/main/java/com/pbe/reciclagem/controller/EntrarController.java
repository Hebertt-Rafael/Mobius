package com.pbe.reciclagem.controller;

import com.pbe.reciclagem.model.Entrar;
import com.pbe.reciclagem.repository.EntrarRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EntrarController {

    @Autowired
    private EntrarRepository entrarRepository;

    // ── GET /entrar, /login ────────────────────────────────────────────────────
    // Renderiza a página completa de login e painel do usuário autenticado
    @GetMapping({"/entrar", "/login"})
    public String paginaEntrar() {
        return "entrar/index";
    }

    // ── GET /cadastro ──────────────────────────────────────────────────────────
    // Renderiza a página dedicada de cadastro de novos clientes ou empresas
    @GetMapping("/cadastro")
    public String paginaCadastro(Model model) {
        model.addAttribute("entrar", new Entrar());
        return "cadastro/index";
    }

    // ── POST /cadastro ─────────────────────────────────────────────────────────
    // Recebe formulário de novo cadastro via Spring MVC
    @PostMapping("/cadastro")
    public String realizarCadastro(
            @Valid @ModelAttribute("entrar") Entrar entrar,
            BindingResult result,
            @RequestParam(name = "confirmarSenha", required = false) String confirmarSenha,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "cadastro/index";
        }

        if (confirmarSenha != null && !confirmarSenha.equals(entrar.getSenha())) {
            result.rejectValue("senha", "senha.divergente", "As senhas digitadas não coincidem.");
            return "cadastro/index";
        }

        // Verificar e-mail duplicado
        if (entrarRepository.findByEmail(entrar.getEmail()).isPresent()) {
            result.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado.");
            return "cadastro/index";
        }

        entrarRepository.save(entrar);

        redirectAttributes.addFlashAttribute("sucesso", true);
        return "redirect:/entrar";
    }

    // ── GET /sair, /logout ─────────────────────────────────────────────────────
    @GetMapping({"/sair", "/logout"})
    public String realizarLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/entrar";
    }
}
