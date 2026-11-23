package com.pbe.reciclagem.controller;

import com.pbe.reciclagem.model.Entrar;
import com.pbe.reciclagem.model.Pontos;
import com.pbe.reciclagem.repository.EntrarRepository;
import com.pbe.reciclagem.repository.PontosRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CadastroRestController {

    @Autowired
    private EntrarRepository entrarRepository;

    @Autowired
    private PontosRepository pontosRepository;

    // ── GET /api/pontos ────────────────────────────────────────────────────────
    @GetMapping("/pontos")
    public List<Pontos> listarPontos(
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

    // ── POST /api/cadastro ─────────────────────────────────────────────────────
    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, Object>> cadastrar(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String tipoCadastro = payload.getOrDefault("tipoCadastro", "cliente");
        String nome = payload.getOrDefault("nome", "").trim();
        String sobrenome = payload.getOrDefault("sobrenome", "").trim();
        String razaoSocial = payload.getOrDefault("razaoSocial", "").trim();
        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        String senha = payload.getOrDefault("senha", "").trim();
        String telefone = payload.getOrDefault("telefone", "").trim();
        String cidade = payload.getOrDefault("cidade", "").trim();
        String interesse = payload.getOrDefault("interesse", "coleta").trim();
        String cnpj = payload.getOrDefault("cnpj", "").trim();
        String responsavel = payload.getOrDefault("responsavel", "").trim();

        if (!StringUtils.hasText(email)) {
            response.put("ok", false);
            response.put("erro", "O e-mail é obrigatório.");
            return ResponseEntity.badRequest().body(response);
        }

        if (entrarRepository.findByEmail(email).isPresent()) {
            response.put("ok", false);
            response.put("erro", "Este e-mail já está cadastrado.");
            return ResponseEntity.badRequest().body(response);
        }

        if ("cliente".equalsIgnoreCase(tipoCadastro) && (!StringUtils.hasText(nome) || !StringUtils.hasText(sobrenome))) {
            response.put("ok", false);
            response.put("erro", "Nome e sobrenome são obrigatórios para cadastro de cliente.");
            return ResponseEntity.badRequest().body(response);
        }

        if ("empresa".equalsIgnoreCase(tipoCadastro) && (!StringUtils.hasText(razaoSocial) || !StringUtils.hasText(cnpj))) {
            response.put("ok", false);
            response.put("erro", "Razão social e CNPJ são obrigatórios para cadastro de empresa.");
            return ResponseEntity.badRequest().body(response);
        }

        Entrar novo = new Entrar();
        novo.setTipoCadastro(tipoCadastro);
        novo.setNome(nome);
        novo.setSobrenome(sobrenome);
        novo.setRazaoSocial(razaoSocial);
        novo.setEmail(email);
        novo.setSenha(senha);
        novo.setTelefone(telefone);
        novo.setCidade(cidade);
        novo.setInteresse(interesse);
        novo.setCnpj(cnpj);
        novo.setResponsavel(responsavel);

        entrarRepository.save(novo);

        // Salvar na sessão HTTP
        if (session != null) {
            session.setAttribute("usuarioLogado", novo);
        }

        Map<String, Object> usuarioData = toUsuarioMap(novo);

        response.put("ok", true);
        response.put("usuario", usuarioData);
        response.put("mensagem", "Cadastro realizado com sucesso!");
        return ResponseEntity.ok(response);
    }

    // ── POST /api/login ────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        String senha = payload.getOrDefault("senha", "").trim();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) {
            response.put("ok", false);
            response.put("erro", "Por favor, preencha seu e-mail e senha.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Entrar> opt = entrarRepository.findByEmail(email);
        if (opt.isEmpty()) {
            response.put("ok", false);
            response.put("erro", "Usuário não encontrado. Verifique o e-mail ou crie uma conta.");
            return ResponseEntity.badRequest().body(response);
        }

        Entrar usuario = opt.get();
        if (StringUtils.hasText(usuario.getSenha()) && !usuario.getSenha().equals(senha)) {
            response.put("ok", false);
            response.put("erro", "Senha incorreta. Tente novamente ou use a recuperação de senha.");
            return ResponseEntity.badRequest().body(response);
        }

        // Salvar na sessão HTTP do servidor
        if (session != null) {
            session.setAttribute("usuarioLogado", usuario);
        }

        Map<String, Object> usuarioData = toUsuarioMap(usuario);

        response.put("ok", true);
        response.put("usuario", usuarioData);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/usuario/sessao ────────────────────────────────────────────────
    @GetMapping("/usuario/sessao")
    public ResponseEntity<Map<String, Object>> obterSessao(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            Entrar usuario = (Entrar) session.getAttribute("usuarioLogado");
            response.put("autenticado", true);
            response.put("usuario", toUsuarioMap(usuario));
        } else {
            response.put("autenticado", false);
        }
        return ResponseEntity.ok(response);
    }

    // ── POST /api/logout ───────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/usuario/atualizar ─────────────────────────────────────────────
    @PutMapping("/usuario/atualizar")
    public ResponseEntity<Map<String, Object>> atualizarUsuario(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        Optional<Entrar> opt = entrarRepository.findByEmail(email);
        if (opt.isEmpty()) {
            response.put("ok", false);
            response.put("erro", "Usuário não encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        Entrar usuario = opt.get();
        if (payload.containsKey("nome") && StringUtils.hasText(payload.get("nome"))) {
            usuario.setNome(payload.get("nome").trim());
        }
        if (payload.containsKey("telefone")) {
            usuario.setTelefone(payload.get("telefone").trim());
        }
        if (payload.containsKey("cidade")) {
            usuario.setCidade(payload.get("cidade").trim());
        }
        if (payload.containsKey("interesse")) {
            usuario.setInteresse(payload.get("interesse").trim());
        }

        entrarRepository.save(usuario);

        if (session != null) {
            session.setAttribute("usuarioLogado", usuario);
        }

        response.put("ok", true);
        response.put("usuario", toUsuarioMap(usuario));
        response.put("mensagem", "Dados atualizados com sucesso!");
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/usuario/senha ─────────────────────────────────────────────────
    @PutMapping("/usuario/senha")
    public ResponseEntity<Map<String, Object>> atualizarSenha(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        String novaSenha = payload.getOrDefault("novaSenha", "").trim();

        if (!StringUtils.hasText(novaSenha)) {
            response.put("ok", false);
            response.put("erro", "A nova senha não pode ser vazia.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Entrar> opt = entrarRepository.findByEmail(email);
        if (opt.isEmpty()) {
            response.put("ok", false);
            response.put("erro", "Usuário não encontrado para este e-mail.");
            return ResponseEntity.badRequest().body(response);
        }

        Entrar usuario = opt.get();
        usuario.setSenha(novaSenha);
        entrarRepository.save(usuario);

        if (session != null) {
            session.setAttribute("usuarioLogado", usuario);
        }

        response.put("ok", true);
        response.put("mensagem", "Senha redefinida com sucesso!");
        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/usuario ────────────────────────────────────────────────────
    @DeleteMapping("/usuario")
    public ResponseEntity<Map<String, Object>> excluirConta(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        Optional<Entrar> opt = entrarRepository.findByEmail(email);
        if (opt.isPresent()) {
            entrarRepository.delete(opt.get());
        }

        if (session != null) {
            session.invalidate();
        }

        response.put("ok", true);
        response.put("mensagem", "Conta excluída com sucesso.");
        return ResponseEntity.ok(response);
    }

    // ── Helper para mapear Entrar para Map ─────────────────────────────────────
    private Map<String, Object> toUsuarioMap(Entrar u) {
        String nomeExibido = "empresa".equalsIgnoreCase(u.getTipoCadastro()) ? u.getRazaoSocial() : u.getNome();
        String nomeCompleto = "empresa".equalsIgnoreCase(u.getTipoCadastro()) 
                ? u.getRazaoSocial() 
                : ((u.getNome() != null ? u.getNome() : "") + " " + (u.getSobrenome() != null ? u.getSobrenome() : "")).trim();

        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("tipoCadastro", u.getTipoCadastro());
        m.put("nome", nomeExibido);
        m.put("sobrenome", u.getSobrenome());
        m.put("nomeCompleto", nomeCompleto);
        m.put("razaoSocial", u.getRazaoSocial());
        m.put("email", u.getEmail());
        m.put("telefone", u.getTelefone());
        m.put("cidade", u.getCidade());
        m.put("interesse", u.getInteresse());
        m.put("cnpj", u.getCnpj());
        m.put("responsavel", u.getResponsavel());
        return m;
    }
}
