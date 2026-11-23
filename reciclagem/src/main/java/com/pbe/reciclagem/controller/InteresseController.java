package com.pbe.reciclagem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InteresseController {

    // ── GET /educacao ──────────────────────────────────────────────────────────
    @GetMapping("/educacao")
    public String paginaEducacao() {
        return "interesse/educacao";
    }

    // ── GET /compostagem ───────────────────────────────────────────────────────
    @GetMapping("/compostagem")
    public String paginaCompostagem() {
        return "interesse/compostagem";
    }

    // ── GET /voluntariado ──────────────────────────────────────────────────────
    @GetMapping("/voluntariado")
    public String paginaVoluntariado() {
        return "interesse/voluntariado";
    }
}
