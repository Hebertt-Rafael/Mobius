package com.pbe.reciclagem;

import com.pbe.reciclagem.model.Entrar;
import com.pbe.reciclagem.model.Pontos;
import com.pbe.reciclagem.repository.EntrarRepository;
import com.pbe.reciclagem.repository.PontosRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Popula as tabelas na primeira execução se estiverem vazias.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PontosRepository pontosRepository;
    private final EntrarRepository entrarRepository;

    public DataSeeder(PontosRepository pontosRepository, EntrarRepository entrarRepository) {
        this.pontosRepository = pontosRepository;
        this.entrarRepository = entrarRepository;
    }

    @Override
    public void run(String @NonNull ... args) {
        // ── Usuário de Teste Padrão ──────────────────────────────────────────
        if (entrarRepository.count() == 0) {
            Entrar demoUser = new Entrar();
            demoUser.setTipoCadastro("cliente");
            demoUser.setNome("Laura");
            demoUser.setSobrenome("Silva");
            demoUser.setEmail("usuario@exemplo.com");
            demoUser.setSenha("123456");
            demoUser.setTelefone("(11) 98765-4321");
            demoUser.setCidade("São Paulo - SP");
            demoUser.setInteresse("coleta");
            entrarRepository.save(demoUser);
            System.out.println("✅ DataSeeder: Usuário de teste criado (usuario@exemplo.com / 123456).");
        }

        if (pontosRepository.count() > 0) {
            return; // Banco já populado — não repete
        }

        List<Pontos> pontos = new ArrayList<>();

        // ── Pontos Base (vindos do front-end original) ─────────────────────────
        pontos.add(new Pontos("Ecoponto Central",               "SP", "Sao Paulo",      "Vila Mariana", "Rua Vergueiro, 2100",           "Papel, plastico, vidro e metal"));
        pontos.add(new Pontos("Coleta Verde Zona Norte",        "SP", "Sao Paulo",      "Santana",      "Avenida Braz Leme, 1350",       "Papel, plastico e eletronicos"));
        pontos.add(new Pontos("Ponto Sustentavel Cambui",       "SP", "Campinas",       "Cambui",       "Rua Coronel Quirino, 820",      "Vidro, metal e oleo de cozinha"));
        pontos.add(new Pontos("Ecoponto Ouro Verde",            "SP", "Campinas",       "Ouro Verde",   "Avenida Ruy Rodrigues, 5050",   "Papel, plastico e entulho leve"));
        pontos.add(new Pontos("Ecoponto Lapa",                  "RJ", "Rio de Janeiro", "Lapa",         "Rua do Riachuelo, 120",         "Papel, plastico, vidro e metal"));
        pontos.add(new Pontos("Coleta Seletiva Ipanema",        "RJ", "Rio de Janeiro", "Ipanema",      "Rua Visconde de Piraja, 500",   "Papel, plastico e vidro"));
        pontos.add(new Pontos("Ecoponto Savassi",               "MG", "Belo Horizonte", "Savassi",      "Av. Cristovao Colombo, 3000",   "Papel, plastico, vidro e metal"));
        pontos.add(new Pontos("Ponto de Coleta Pampulha",       "MG", "Belo Horizonte", "Pampulha",     "Av. Otacilio Negrao de Lima, 2000", "Eletronicos e pilhas"));

        // ── Pontos gerados para os demais estados ──────────────────────────────
        String[][] ufs = {
            {"AC","Rio Branco", "Cruzeiro do Sul", "Sena Madureira", "Tarauaca", "Brasileia"},
                {"AL","Maceio", "Arapiraca", "Palmeira dos Indios", "Penedo", "Rio Largo"},
                {"AP","Macapa", "Santana", "Laranjal do Jari", "Oiapoque", "Mazagao"},
                {"AM","Manaus", "Parintins", "Itacoatiara", "Manacapuru", "Tefé"},
            {"BA","Salvador", "Feira de Santana", "Vitoria da Conquista", "Camaçari", "Ilheus"},
                {"CE","Fortaleza", "Caucaia", "Juazeiro do Norte", "Sobral", "Maracanau"},
                {"DF","Brasilia", "Ceilandia", "Taguatinga", "Gama", "Sobradinho"},
                {"ES","Vitoria", "Vila Velha", "Serra", "Cariacica", "Linhares"},
            {"GO","Goiania", "Aparecida de Goiania", "Anapolis", "Rio Verde", "Catalão"},
                {"MA","Sao Luis", "Imperatriz", "Caxias", "Timon", "Bacabal"},
                {"MT","Cuiaba", "Varzea Grande", "Rondonopolis", "Sinop", "Tangara da Serra"},
                {"MS","Campo Grande", "Dourados", "Tres Lagoas", "Corumba", "Ponta Pora"},
                {"MG","Belo Horizonte", "Contagem", "Uberlandia", "Juiz de Fora", "Montes Claros"},
            {"PA","Belem", "Ananindeua", "Santarém", "Maraba", "Parauapebas"},
                {"PB","Joao Pessoa", "Campina Grande", "Patos", "Bayeux", "Cabedelo"},
                {"PR","Curitiba", "Londrina", "Maringa", "Ponta Grossa", "Cascavel"},
                {"PE","Recife", "Jaboatao dos Guararapes", "Olinda", "Caruaru", "Petrolina"},
            {"PI","Teresina", "Parnaiba", "Picos", "Floriano", "Piripiri"},
                {"RJ","Rio de Janeiro", "Niteroi", "Nova Iguacu", "Duque de Caxias", "Petropolis"},
                {"RN","Natal", "Mossoro", "Parnamirim", "Caico", "Sao Goncalo do Amarante"},
                {"RO","Porto Velho", "Ji-Parana", "Ariquemes", "Vilhena", "Cacoal"},
                {"RR","Boa Vista", "Rorainopolis", "Caracarai", "Mucajai", "Amajari"},
            {"RS","Porto Alegre", "Caxias do Sul", "Pelotas", "Canoas", "Santa Maria"},
                {"SC","Florianopolis", "Joinville", "Blumenau", "Sao Jose", "Chapeco"},
                {"SE","Aracaju", "Nossa Senhora do Socorro", "Lagarto", "Itabaiana", "Estancia"},
                {"SP","Sao Paulo", "Campinas", "Santos", "Sorocaba", "Sao Jose dos Campos"},
            {"TO","Palmas", "Araguaina", "Gurupi", "Porto Nacional", "Paraiso do Tocantins"}
        };

        String[] bairros   = {"Centro", "Jardim", "Vila Nova", "Boa Vista", "Industrial", "Planalto", "Santa Clara", "Sao Jose"};
        String[] materiais = {
            "Papel, plastico, vidro e metal",
            "Papel, plastico e metal",
            "Vidro e oleo de cozinha",
            "Eletronicos e pilhas",
            "Papel e plastico"
        };

        for (String[] ufData : ufs) {
            String uf = ufData[0];
            String cidade = ufData[1];
            for (int i = 0; i < 5; i++) {
                pontos.add(new Pontos(
                    "Ponto de Coleta " + uf + " " + String.format("%02d", i + 1),
                    uf,
                    cidade,
                    bairros[i % bairros.length],
                    "Av. Principal, " + (100 + i),
                    materiais[i % materiais.length]
                ));
            }
        }

        pontosRepository.saveAll(pontos);
        System.out.println("✅ DataSeeder: " + pontos.size() + " pontos de coleta inseridos.");
    }
}
