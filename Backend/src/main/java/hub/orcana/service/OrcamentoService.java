package hub.orcana.service;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.observer.OrcamentoSubject;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrcamentoService implements OrcamentoSubject {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoService.class);

    private final OrcamentoRepository repository;
    private final GerenciadorDeArquivosService gerenciadorService;
    private final EmailService emailService;
    private final List<OrcamentoObserver> observers = new ArrayList<>();
    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;

    public OrcamentoService(
            OrcamentoRepository repository,
            GerenciadorDeArquivosService gerenciadorService,
            EmailService emailService,
            UsuarioRepository usuarioRepository,
            AgendamentoRepository agendamentoRepository
    ) {
        this.repository = repository;
        this.gerenciadorService = gerenciadorService;
        this.emailService = emailService;
        this.attach(emailService);
        this.usuarioRepository = usuarioRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    public void attach(OrcamentoObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(OrcamentoObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Orcamento orcamento) {
        for (OrcamentoObserver observer : observers) {
            observer.updateOrcamento(orcamento);
        }
    }

    private String gerarCodigoOrcamento() {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (repository.findByCodigoOrcamento("ORC-" + uuid).isPresent()) {
            return gerarCodigoOrcamento();
        }
        return "ORC-" + uuid;
    }

    public Orcamento postOrcamento(CadastroOrcamentoInput dados) {
        List<String> urlImagens = new ArrayList<>();

        if (dados.imagemReferencia() != null && !dados.imagemReferencia().isEmpty()) {
            for (MultipartFile file : dados.imagemReferencia()) {
                String urlImagemSalva = gerenciadorService.salvarArquivo(file);
                urlImagens.add(urlImagemSalva);
            }
        }

        String codigo = gerarCodigoOrcamento();

        Usuario usuario = usuarioRepository.findByEmail(dados.email()).orElse(null);

        Orcamento orcamento = new Orcamento(
                codigo,
                dados.nome(),
                dados.email(),
                dados.ideia(),
                dados.tamanho(),
                dados.cores(),
                dados.localCorpo(),
                urlImagens,
                usuario != null ? usuario.getId() : null,
                StatusOrcamento.PENDENTE
        );

        if (usuario != null) {
            orcamento.setUsuario(usuario);
        }

        Orcamento salvo = repository.save(orcamento);

        try {
            notifyObservers(salvo);
        } catch (Exception e) {
            log.error("Falha ao notificar observers: {}", e.getMessage());
        }

        return salvo;
    }

    public List<DetalhesOrcamentoOutput> findAllOrcamentos() {
        return repository.findAll().stream().map(
                orcamento -> new DetalhesOrcamentoOutput(
                        orcamento.getCodigoOrcamento(),
                        orcamento.getNome(),
                        orcamento.getEmail(),
                        orcamento.getIdeia(),
                        orcamento.getTamanho(),
                        orcamento.getCores(),
                        orcamento.getLocalCorpo(),
                        orcamento.getImagemReferencia(),
                        orcamento.getValor(),
                        orcamento.getTempo(),
                        orcamento.getStatus()
                )
        ).toList();
    }

    public List<DetalhesOrcamentoOutput> findOrcamentosByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(
                orcamento -> new DetalhesOrcamentoOutput(
                        orcamento.getCodigoOrcamento(),
                        orcamento.getNome(),
                        orcamento.getEmail(),
                        orcamento.getIdeia(),
                        orcamento.getTamanho(),
                        orcamento.getCores(),
                        orcamento.getLocalCorpo(),
                        orcamento.getImagemReferencia(),
                        orcamento.getValor(),
                        orcamento.getTempo(),
                        orcamento.getStatus()
                )
        ).toList();
    }

    public Orcamento atualizarOrcamento(String codigo, Double tamanho, String localCorpo, String cores, String ideia) {
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

        if (tamanho != null) {
            orcamento.setTamanho(tamanho);
        }
        if (localCorpo != null && !localCorpo.isBlank()) {
            orcamento.setLocalCorpo(localCorpo);
        }
        if (cores != null && !cores.isBlank()) {
            orcamento.setCores(cores);
        }
        if (ideia != null && !ideia.isBlank()) {
            orcamento.setIdeia(ideia);
        }

        return repository.save(orcamento);
    }

    public boolean verificarSeTemAgendamento(String codigo) {
        return agendamentoRepository.findByOrcamentoCodigoOrcamento(codigo).isPresent();
    }

    public void deletarOrcamento(String codigo) {
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        agendamentoRepository.findByOrcamentoCodigoOrcamento(codigo)
                .ifPresent(agendamentoRepository::delete);

        repository.delete(orcamento);
    }

    public DetalhesOrcamentoOutput atualizarOrcamento(String codigo, Map<String, Object> dados) {

        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        if (dados.containsKey("valor") && dados.get("valor") instanceof Number valor) {
            orcamento.setValor(valor.doubleValue());
        }

        if (dados.containsKey("tempo") && dados.get("tempo") instanceof String tempoStr) {
            orcamento.setTempo(Time.valueOf(tempoStr));
        }

        if (dados.containsKey("nome")) orcamento.setNome((String) dados.get("nome"));
        if (dados.containsKey("email")) orcamento.setEmail((String) dados.get("email"));
        if (dados.containsKey("ideia")) orcamento.setIdeia((String) dados.get("ideia"));
        if (dados.containsKey("cores")) orcamento.setCores((String) dados.get("cores"));
        if (dados.containsKey("localCorpo")) orcamento.setLocalCorpo((String) dados.get("localCorpo"));

        if (dados.containsKey("tamanho") && dados.get("tamanho") instanceof Number tamanho) {
            orcamento.setTamanho(tamanho.doubleValue());
        }

        if (dados.containsKey("status") && dados.get("status") instanceof String statusStr) {
            try {
                orcamento.setStatus(StatusOrcamento.valueOf(statusStr));
            } catch (Exception ignored) {}
        }

        Orcamento salvo = repository.save(orcamento);

        // ✅ ENVIA O E-MAIL CERTO
        if (dados.containsKey("valor") && dados.containsKey("tempo")) {
            try {
                emailService.enviaEmailOrcamentoAprovado(
                        salvo.getEmail(),
                        salvo.getNome(),
                        salvo.getCodigoOrcamento(),
                        salvo.getValor(),
                        salvo.getTempo()
                );
                log.info("E-mail de aprovação enviado para {}", salvo.getEmail());
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail de aprovação: {}", e.getMessage());
            }
        }

        return new DetalhesOrcamentoOutput(
                salvo.getCodigoOrcamento(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getIdeia(),
                salvo.getTamanho(),
                salvo.getCores(),
                salvo.getLocalCorpo(),
                salvo.getImagemReferencia(),
                salvo.getValor(),
                salvo.getTempo(),
                salvo.getStatus()
        );
    }

    public DetalhesOrcamentoOutput findByCodigo(String codigo) {

        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        return new DetalhesOrcamentoOutput(
                orcamento.getCodigoOrcamento(),
                orcamento.getNome(),
                orcamento.getEmail(),
                orcamento.getIdeia(),
                orcamento.getTamanho(),
                orcamento.getCores(),
                orcamento.getLocalCorpo(),
                orcamento.getImagemReferencia(),
                orcamento.getValor(),
                orcamento.getTempo(),
                orcamento.getStatus()
        );
    }
}
