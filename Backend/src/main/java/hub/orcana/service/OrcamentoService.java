package hub.orcana.service;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.repository.OrcamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.observer.OrcamentoSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
//@RequiredArgsConstructor comentei devido ao construtor manual, onde implemento o attach do observer
public class OrcamentoService implements OrcamentoSubject{

    private static final Logger log = LoggerFactory.getLogger(OrcamentoService.class);

    private final OrcamentoRepository repository;
    private final GerenciadorDeArquivosService gerenciadorService;
    private final EmailService emailService;
    private final List<OrcamentoObserver> observers = new ArrayList<>();

    public OrcamentoService(OrcamentoRepository repository, GerenciadorDeArquivosService gerenciadorService, EmailService emailService) {
        this.repository = repository;
        this.gerenciadorService = gerenciadorService;
        this.emailService = emailService;
        this.attach(emailService);
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
                urlImagens
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
                        orcamento.getId(),
                        orcamento.getCodigoOrcamento(),
                        orcamento.getNome(),
                        orcamento.getEmail(),
                        orcamento.getIdeia(),
                        orcamento.getValor(),
                        orcamento.getTamanho(),
                        orcamento.getEstilo(),
                        orcamento.getCores(),
                        orcamento.getTempo(),
                        orcamento.getLocalCorpo(),
                        orcamento.getImagemReferencia(),
                        orcamento.getStatus() != null ? orcamento.getStatus().name() : "AGUARDANDO_RESPOSTA"
                )
        ).toList(
        );
    }

    public List<DetalhesOrcamentoOutput> findOrcamentosPorUsuario(Long usuarioId) {
        log.info("Buscando orçamentos para usuário ID: {}", usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(
                orcamento -> new DetalhesOrcamentoOutput(
                        orcamento.getId(),
                        orcamento.getCodigoOrcamento(),
                        orcamento.getNome(),
                        orcamento.getEmail(),
                        orcamento.getIdeia(),
                        orcamento.getValor(),
                        orcamento.getTamanho(),
                        orcamento.getEstilo(),
                        orcamento.getCores(),
                        orcamento.getTempo(),
                        orcamento.getLocalCorpo(),
                        orcamento.getImagemReferencia(),
                        orcamento.getStatus() != null ? orcamento.getStatus().name() : "AGUARDANDO_RESPOSTA"
                )
        ).toList();
    }

}