# Adaptar o front-end ao back-end Java

**Objetivo**: Substituir todo o código atual de back‑end (projeto Maven existente) por uma aplicação Java Spring Boot que sirva os arquivos estáticos do front‑end e implemente a lógica de negócio, armazenando dados em JSON e MySQL.

## Revisão pelo Usuário Necessária

> [!IMPORTANT]
> Esta mudança removerá o código Java atual (pasta `src`) e criará uma nova estrutura de projeto. Certifique‑se de que não há código personalizado que precise ser preservado.

## Perguntas em Aberto

> [!WARNING]
> - **Quais formulários ou interações do front‑end precisam ser convertidos em rotas de API?** (ex.: cadastro de voluntários, contato, ponto de coleta).
> - **Existe algum esquema de banco de dados pré‑definido ou precisamos criar as tabelas a partir do zero?**
> - **O site será hospedado localmente ou em um servidor específico que requer algum perfil de conexão (host, porta, usuário, senha)?**

## Proposta de Alterações

---
### 1. Reestruturação do Projeto

#### [DELETE] [pom.xml](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/pom.xml)
- O `pom.xml` atual será substituído por um novo configurado para Spring Boot.

#### [NEW] [pom.xml](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/pom.xml)
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>br.com.recyclage</groupId>
    <artifactId>reciclagem</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web (serves static content & REST) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Spring Data JPA (MySQL) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Jackson (JSON) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <!-- Lombok (optional, facilita POJOs) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---
### 2. Estrutura de Código Fonte

#### [NEW] [src/main/java/br/com/recyclage/ReciclagemApplication.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/java/br/com/recyclage/ReciclagemApplication.java)
```java
package br.com.recyclage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReciclagemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReciclagemApplication.class, args);
    }
}
```

#### [NEW] [src/main/java/br/com/recyclage/controller/StaticPageController.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/java/br/com/recyclage/controller/StaticPageController.java)
```java
package br.com.recyclage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Mapeia as páginas HTML do front‑end. Os arquivos estão em src/main/resources/static.
 */
@Controller
public class StaticPageController {
    @GetMapping({"/", "/index.html"})
    public String index() { return "index.html"; }

    @GetMapping("/cadastro.html")
    public String cadastro() { return "cadastro.html"; }

    @GetMapping("/contato.html")
    public String contato() { return "contato.html"; }

    // Adicionar demais mapeamentos conforme necessário
}
```

---
### 3. Camada de Persistência (MySQL)

#### [NEW] [src/main/java/br/com/recyclage/model/Voluntario.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/java/br/com/recyclage/model/Voluntario.java)
```java
package br.com.recyclage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "voluntario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voluntario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    // outros campos que existam no formulário de cadastro
}
```

#### [NEW] [src/main/java/br/com/recyclage/repository/VoluntarioRepository.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/java/br/com/recyclage/repository/VoluntarioRepository.java)
```java
package br.com.recyclage.repository;

import br.com.recyclage.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> { }
```

---
### 4. Serviços e Controladores REST (JSON)

#### [NEW] [src/main/java/br/com/recyclage/controller/VoluntarioController.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/java/br/com/recyclage/controller/VoluntarioController.java)
```java
package br.com.recyclage.controller;

import br.com.recyclage.model.Voluntario;
import br.com.recyclage.repository.VoluntarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voluntarios")
public class VoluntarioController {
    private final VoluntarioRepository repo;

    public VoluntarioController(VoluntarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Voluntario> listar() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<Voluntario> criar(@RequestBody Voluntario voluntario) {
        Voluntario salvo = repo.save(voluntario);
        return ResponseEntity.ok(salvo);
    }

    // TODO: endpoints para atualização e remoção se necessário
}
```

---
### 5. Configuração do MySQL e JSON

#### [NEW] [src/main/resources/application.properties](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/main/resources/application.properties)
```properties
# ==== Configurações do Spring ==== 
server.port=8080

# ==== Banco de Dados MySQL ==== 
spring.datasource.url=jdbc:mysql://localhost:3306/reciclagem_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# ==== JSON (Jackson) ==== 
spring.jackson.serialization.indent_output=true
```

> **Nota:** Substitua `seu_usuario` e `sua_senha` pelos credenciais corretas. O banco `reciclagem_db` pode ser criado manualmente ou deixado o JPA criar via `ddl-auto=update`.

---
### 6. Migração dos Arquivos Estáticos

1. Criar a pasta `src/main/resources/static`.
2. Copiar todos os arquivos HTML, CSS, JS, imagens e demais assets da pasta `site/` para dentro de `static` mantendo a mesma estrutura (ex.: `static/css/`, `static/img/`).
3. Ajustar caminhos relativos nos HTML se necessário (ex.: `<link href="css/style.css" ...>` continuará funcionando porque será resolvido a partir de `static`.

---
### 7. Testes Automáticos

#### [NEW] [src/test/java/br/com/recyclage/VoluntarioControllerTest.java](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/src/test/java/br/com/recyclage/VoluntarioControllerTest.java)
```java
package br.com.recyclage;

import br.com.recyclage.model.Voluntario;
import br.com.recyclage.repository.VoluntarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VoluntarioControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private VoluntarioRepository repo;

    @Test
    void criarVoluntario_retorna200() throws Exception {
        String json = "{\"nome\":\"Ana\",\"email\":\"ana@example.com\",\"telefone\":\"123456789\",\"cidade\":\"São Paulo\"}";
        mockMvc.perform(post("/api/voluntarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }
}
```

---
### 8. Script de Inicialização (Opcional)

#### [NEW] [run.bat](file:///c:/Users/55747284860/OneDrive%20-%20SESISENAISP%20-%20Escolas/TCC/reciclagem/run.bat)
```bat
@echo off
rem Compila e inicia a aplicação Spring Boot
mvn clean package
java -jar target/reciclagem-0.0.1-SNAPSHOT.jar
```

## Plano de Verificação

### Testes Automatizados
- Execução do `mvn test` para validar os controladores REST.
- Verificar se o schema do MySQL foi criado corretamente (`voluntario` table).

### Verificação Manual
- Iniciar a aplicação (`run.bat`).
- Acessar `http://localhost:8080/` e navegar por todas as páginas estáticas.
- Submeter o formulário de cadastro (se houver) e confirmar que o registro aparece na tabela MySQL.
- Testar chamadas JSON via `curl` ou Postman (`GET /api/voluntarios`, `POST /api/voluntarios`).

---
**Próximos passos**
1. Aprovação deste plano pelo usuário.
2. Criação da estrutura de diretórios e arquivos listados acima.
3. Implementação dos códigos e migração dos ativos estáticos.
4. Execução dos testes e validação.

Por favor, revise o plano e responda às perguntas em aberto para que possamos prosseguir.
