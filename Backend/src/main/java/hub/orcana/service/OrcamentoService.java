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

        if (dados.imagemReferencia() != null && !dados.imagemReferencia().isEmpty()) {
            for (MultipartFile file : dados.imagemReferencia()) {
                String urlImagemSalva = gerenciadorService.salvarArquivo(file);
                urlImagens.add(urlImagemSalva);
            }
        }

        // gera codigo unico
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
                        orcamento.getImagemReferencia()
                )
        ).toList(
        );
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

}