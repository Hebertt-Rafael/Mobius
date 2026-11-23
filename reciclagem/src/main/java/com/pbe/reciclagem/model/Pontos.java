package com.pbe.reciclagem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pontos_coleta")
public class Pontos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do ponto é obrigatório")
    private String nome;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2)
    @Column(length = 2)
    private String uf;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    private String endereco;

    @Column(length = 300)
    private String materiais;

    // ── Constructors ───────────────────────────────────────────────────────────

    public Pontos() {}

    public Pontos(String nome, String uf, String cidade, String bairro, String endereco, String materiais) {
        this.nome = nome;
        this.uf = uf != null ? uf.toUpperCase() : null;
        this.cidade = cidade;
        this.bairro = bairro;
        this.endereco = endereco;
        this.materiais = materiais;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf != null ? uf.toUpperCase() : null; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getMateriais() { return materiais; }
    public void setMateriais(String materiais) { this.materiais = materiais; }
}
