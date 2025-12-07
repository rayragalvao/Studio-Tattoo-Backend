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

    public OrcamentoService(OrcamentoRepository repository,
                            GerenciadorDeArquivosService gerenciadorService,
                            EmailService emailService,
                            UsuarioRepository usuarioRepository,
                            AgendamentoRepository agendamentoRepository) {
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

    private Long verificarEmailExistente(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElse(null);
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
            log.info("Orçamento {} vinculado ao usuário existente: {}", codigo, usuario.getEmail());
        } else {
            log.info("Orçamento {} criado sem vínculo com usuário (email não cadastrado)", codigo);
        }

        Orcamento salvo = repository.save(orcamento);

        try {
            notifyObservers(salvo);
        } catch (Exception e) {
            log.error("Falha ao notificar Observers de novo orçamento {}: {}", salvo.getCodigoOrcamento(), e.getMessage());
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
                orcamento -> {
                    log.info("Orçamento {}: imagemReferencia = {}", orcamento.getCodigoOrcamento(), orcamento.getImagemReferencia());
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
        ).toList();
    }

    public Orcamento atualizarOrcamento(String codigo, Double tamanho, String localCorpo, String cores, String ideia) {
        log.info("Atualizando orçamento: {}", codigo);
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

        if (tamanho != null) orcamento.setTamanho(tamanho);
        if (localCorpo != null && !localCorpo.isBlank()) orcamento.setLocalCorpo(localCorpo);
        if (cores != null && !cores.isBlank()) orcamento.setCores(cores);
        if (ideia != null && !ideia.isBlank()) orcamento.setIdeia(ideia);

        return repository.save(orcamento);
    }

    public boolean verificarSeTemAgendamento(String codigo) {
        return agendamentoRepository.findByOrcamentoCodigoOrcamento(codigo).isPresent();
    }

    public void deletarOrcamento(String codigo) {
        log.info("Deletando orçamento: {}", codigo);
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

        agendamentoRepository.findByOrcamentoCodigoOrcamento(codigo).ifPresent(agendamento -> {
            log.info("Deletando agendamento relacionado ao orçamento {}: {}", codigo, agendamento.getId());
            agendamentoRepository.delete(agendamento);
            log.info("Agendamento {} deletado com sucesso", agendamento.getId());
        });

        repository.delete(orcamento);
        log.info("Orçamento {} deletado com sucesso", codigo);
    }

    public DetalhesOrcamentoOutput atualizarOrcamento(String codigo, Map<String, Object> dados) {
        log.info("Buscando orçamento com código: {}", codigo);
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

        if (dados.containsKey("valor")) {
            Object valorObj = dados.get("valor");
            if (valorObj instanceof Number) {
                orcamento.setValor(((Number) valorObj).doubleValue());
            }
        }

        if (dados.containsKey("tempo")) {
            Object tempoObj = dados.get("tempo");
            if (tempoObj instanceof String) {
                try {
                    orcamento.setTempo(java.time.LocalTime.parse((String) tempoObj));
                } catch (Exception e) {
                    throw new RuntimeException("Formato de tempo inválido. Use HH:mm:ss");
                }
            }
        }

        if (dados.containsKey("nome") && dados.get("nome") != null) orcamento.setNome((String) dados.get("nome"));
        if (dados.containsKey("email") && dados.get("email") != null) orcamento.setEmail((String) dados.get("email"));
        if (dados.containsKey("ideia") && dados.get("ideia") != null) orcamento.setIdeia((String) dados.get("ideia"));
        if (dados.containsKey("tamanho") && dados.get("tamanho") != null) {
            Object tamanhoObj = dados.get("tamanho");
            if (tamanhoObj instanceof Number) orcamento.setTamanho(((Number) tamanhoObj).doubleValue());
        }
        if (dados.containsKey("cores") && dados.get("cores") != null) orcamento.setCores((String) dados.get("cores"));
        if (dados.containsKey("localCorpo") && dados.get("localCorpo") != null) orcamento.setLocalCorpo((String) dados.get("localCorpo"));
        if (dados.containsKey("status") && dados.get("status") != null) {
            String statusStr = (String) dados.get("status");
            try {
                orcamento.setStatus(StatusOrcamento.valueOf(statusStr));
            } catch (Exception e) {
                log.warn("Status inválido: {}", statusStr);
            }
        }

        Orcamento salvo = repository.save(orcamento);

        if (dados.containsKey("valor") && dados.containsKey("tempo")) {
            try {
                String assunto = "Orçamento Aprovado - Júpiter Frito";
                String corpo = String.format(
                        "Olá %s,\n\nSeu orçamento foi aprovado!\n\nCódigo: %s\nValor: R$ %.2f\nTempo estimado: %s\n\nEm breve entraremos em contato para agendar sua sessão.\n\nAtenciosamente,\nEquipe Júpiter Frito",
                        salvo.getNome(),
                        salvo.getCodigoOrcamento(),
                        salvo.getValor(),
                        salvo.getTempo()
                );
                emailService.enviarTextoSimples(salvo.getEmail(), assunto, corpo);
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
        log.info("Buscando orçamento com código: {}", codigo);
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

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
