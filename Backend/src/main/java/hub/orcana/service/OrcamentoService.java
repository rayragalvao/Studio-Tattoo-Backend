package hub.orcana.service;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.Usuario;
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
//@RequiredArgsConstructor comentei devido ao construtor manual, onde implemento o attach do observer
public class OrcamentoService implements OrcamentoSubject{

    private static final Logger log = LoggerFactory.getLogger(OrcamentoService.class);

    private final OrcamentoRepository repository;
    private final GerenciadorDeArquivosService gerenciadorService;
    private final EmailService emailService;
    private final List<OrcamentoObserver> observers = new ArrayList<>();
    private final UsuarioRepository usuarioRepository;

    public OrcamentoService(OrcamentoRepository repository, GerenciadorDeArquivosService gerenciadorService, EmailService emailService, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.gerenciadorService = gerenciadorService;
        this.emailService = emailService;
        this.attach(emailService);
        this.usuarioRepository = usuarioRepository;
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
        Long usuario_id = verificarEmailExistente(dados.email());

        if (dados.imagemReferencia() != null && !dados.imagemReferencia().isEmpty()) {
            for (MultipartFile file : dados.imagemReferencia()) {
                String urlImagemSalva = gerenciadorService.salvarArquivo(file);
                urlImagens.add(urlImagemSalva);
            }
        }

        // gera codigo unico
        String codigo = gerarCodigoOrcamento();

        Orcamento orcamento = new Orcamento(
                codigo,
                dados.nome(),
                dados.email(),
                dados.ideia(),
                dados.tamanho(),
                dados.cores(),
                dados.localCorpo(),
                urlImagens,
                usuario_id,
                StatusOrcamento.PENDENTE
        );

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
                        orcamento.getStatus()
                )
        ).toList(
        );
    }

    public DetalhesOrcamentoOutput atualizarOrcamento(String codigo, Map<String, Object> dados) {
        log.info("Buscando orçamento com código: {}", codigo);
        Orcamento orcamento = repository.findByCodigoOrcamento(codigo)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado: " + codigo));

        log.info("Orçamento encontrado: {}. Atualizando campos...", codigo);

        // Atualizar valor se fornecido
        if (dados.containsKey("valor")) {
            Object valorObj = dados.get("valor");
            if (valorObj instanceof Number) {
                Double valor = ((Number) valorObj).doubleValue();
                log.info("Atualizando valor: {}", valor);
                orcamento.setValor(valor);
            }
        }

        // Atualizar tempo se fornecido
        if (dados.containsKey("tempo")) {
            Object tempoObj = dados.get("tempo");
            if (tempoObj instanceof String) {
                String tempoStr = (String) tempoObj;
                log.info("Atualizando tempo: {}", tempoStr);
                try {
                    orcamento.setTempo(java.time.LocalTime.parse(tempoStr));
                } catch (Exception e) {
                    log.error("Erro ao parsear tempo: {}", tempoStr, e);
                    throw new RuntimeException("Formato de tempo inválido. Use HH:mm:ss");
                }
            }
        }

        // Atualizar outros campos se fornecidos
        if (dados.containsKey("nome") && dados.get("nome") != null) {
            orcamento.setNome((String) dados.get("nome"));
        }
        if (dados.containsKey("email") && dados.get("email") != null) {
            orcamento.setEmail((String) dados.get("email"));
        }
        if (dados.containsKey("ideia") && dados.get("ideia") != null) {
            orcamento.setIdeia((String) dados.get("ideia"));
        }
        if (dados.containsKey("tamanho") && dados.get("tamanho") != null) {
            Object tamanhoObj = dados.get("tamanho");
            if (tamanhoObj instanceof Number) {
                orcamento.setTamanho(((Number) tamanhoObj).doubleValue());
            }
        }
        if (dados.containsKey("cores") && dados.get("cores") != null) {
            orcamento.setCores((String) dados.get("cores"));
        }
        if (dados.containsKey("localCorpo") && dados.get("localCorpo") != null) {
            orcamento.setLocalCorpo((String) dados.get("localCorpo"));
        }
        if (dados.containsKey("status") && dados.get("status") != null) {
            String statusStr = (String) dados.get("status");
            try {
                orcamento.setStatus(StatusOrcamento.valueOf(statusStr));
            } catch (Exception e) {
                log.warn("Status inválido: {}", statusStr);
            }
        }

        log.info("Salvando orçamento atualizado no banco de dados");
        Orcamento salvo = repository.save(orcamento);

        // Enviar email ao cliente informando o orçamento aprovado
        if (dados.containsKey("valor") && dados.containsKey("tempo")) {
            try {
                log.info("Enviando e-mail de aprovação para: {}", salvo.getEmail());
                String assunto = "Orçamento Aprovado - Júpiter Frito";
                String corpo = String.format(
                        "Olá %s,\n\n" +
                                "Seu orçamento foi aprovado!\n\n" +
                                "Código: %s\n" +
                                "Valor: R$ %.2f\n" +
                                "Tempo estimado: %s\n\n" +
                                "Em breve entraremos em contato para agendar sua sessão.\n\n" +
                                "Atenciosamente,\n" +
                                "Equipe Júpiter Frito",
                        salvo.getNome(),
                        salvo.getCodigoOrcamento(),
                        salvo.getValor(),
                        salvo.getTempo()
                );
                emailService.enviarTextoSimples(salvo.getEmail(), assunto, corpo);
                log.info("E-mail de aprovação enviado com sucesso para: {}", salvo.getEmail());
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail de aprovação: {}", e.getMessage());
                // Não lança exceção para não interromper o fluxo
            }
        }

        log.info("Orçamento {} salvo com sucesso", codigo);
        return new DetalhesOrcamentoOutput(
                salvo.getCodigoOrcamento(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getIdeia(),
                salvo.getTamanho(),
                salvo.getCores(),
                salvo.getLocalCorpo(),
                salvo.getImagemReferencia(),
                salvo.getStatus()
        );
    }

}