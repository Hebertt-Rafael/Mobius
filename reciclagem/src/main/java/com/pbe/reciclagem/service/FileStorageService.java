package com.pbe.reciclagem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final List<String> EXTENSOES_PERMITIDAS = Arrays.asList("jpg", "jpeg", "png", "webp");

    private final Path uploadLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            log.error("Não foi possível inicializar o diretório de uploads em: {}", this.uploadLocation, e);
            throw new RuntimeException("Falha ao criar diretório de upload.", e);
        }
    }

    /**
     * Salva o arquivo de upload de forma segura e retorna o nome gerado.
     */
    public String salvarArquivo(MultipartFile file, String subDiretorio) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String nomeOriginal = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");

        // Validação de Path Traversal
        if (nomeOriginal.contains("..")) {
            throw new IllegalArgumentException("Nome de arquivo contém sequência de caminho inválida: " + nomeOriginal);
        }

        // Validação da extensão
        String extensao = "";
        int pontoIndex = nomeOriginal.lastIndexOf('.');
        if (pontoIndex > 0) {
            extensao = nomeOriginal.substring(pontoIndex + 1).toLowerCase();
        }

        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw new IllegalArgumentException("Extensão de arquivo não permitida. Envie apenas imagens (JPG, PNG, WEBP).");
        }

        String novoNome = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "." + extensao;

        try {
            Path pastaDestino = this.uploadLocation.resolve(subDiretorio).normalize();
            if (!Files.exists(pastaDestino)) {
                Files.createDirectories(pastaDestino);
            }

            Path destinoCompleto = pastaDestino.resolve(novoNome);
            Files.copy(file.getInputStream(), destinoCompleto, StandardCopyOption.REPLACE_EXISTING);
            log.info("Arquivo salvo com sucesso em: {}", destinoCompleto);
            return novoNome;
        } catch (IOException e) {
            log.error("Erro ao gravar arquivo em disco: {}", nomeOriginal, e);
            throw new RuntimeException("Falha ao armazenar arquivo.", e);
        }
    }

    /**
     * Exclui arquivo físico anterior ao atualizar a foto.
     */
    public void removerArquivo(String nomeArquivo, String subDiretorio) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) return;
        try {
            Path arquivo = this.uploadLocation.resolve(subDiretorio).resolve(nomeArquivo).normalize();
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            log.warn("Não foi possível excluir o arquivo antigo: {}", nomeArquivo);
        }
    }
}