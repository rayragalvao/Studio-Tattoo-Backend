package hub.orcana.service;

import hub.orcana.dto.historico.ClienteHistoricoResumoOutput;
import hub.orcana.dto.historico.DetalhesHistoricoClienteOutput;
import hub.orcana.dto.historico.HistoricoClientesOutput;
import hub.orcana.dto.historico.SessaoHistoricoOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HistoricoClientesService {

    private static final String ESTILO_NAO_INFORMADO = "Nao informado";

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoClientesService(
            AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public HistoricoClientesOutput listar(String busca) {
        List<Agendamento> concluidos = agendamentoRepository
                .findByStatusComUsuarioEOrcamento(StatusAgendamento.CONCLUIDO);

        Map<Long, List<Agendamento>> sessoesPorCliente = new LinkedHashMap<>();
        for (Agendamento agendamento : concluidos) {
            if (agendamento.getUsuario() == null || agendamento.getUsuario().isAdmin()) {
                continue;
            }
            sessoesPorCliente
                    .computeIfAbsent(agendamento.getUsuario().getId(), id -> new ArrayList<>())
                    .add(agendamento);
        }

        String termo = normalizar(busca);
        List<ClienteHistoricoResumoOutput> clientes = sessoesPorCliente.values().stream()
                .map(this::resumir)
                .filter(cliente -> correspondeABusca(cliente, termo))
                .toList();

        int totalSessoes = clientes.stream()
                .mapToInt(ClienteHistoricoResumoOutput::quantidadeSessoes)
                .sum();
        double receita = clientes.stream()
                .map(ClienteHistoricoResumoOutput::gastoTotal)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        return new HistoricoClientesOutput(clientes.size(), totalSessoes, receita, clientes);
    }

    @Transactional(readOnly = true)
    public DetalhesHistoricoClienteOutput detalhar(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .filter(usuario -> !usuario.isAdmin())
                .orElseThrow(() -> new DependenciaNaoEncontradaException("Cliente"));

        List<Agendamento> agendamentos = agendamentoRepository
                .findByUsuarioIdAndStatusComOrcamento(clienteId, StatusAgendamento.CONCLUIDO);
        List<SessaoHistoricoOutput> sessoes = agendamentos.stream()
                .map(this::mapearSessao)
                .toList();

        return new DetalhesHistoricoClienteOutput(
                cliente.getId(),
                cliente.getNome(),
                obterIniciais(cliente.getNome()),
                obterEstilos(agendamentos),
                sessoes.size(),
                somarValores(agendamentos),
                agendamentos.isEmpty() ? null : agendamentos.getFirst().getDataHora(),
                sessoes
        );
    }

    private ClienteHistoricoResumoOutput resumir(List<Agendamento> agendamentos) {
        Agendamento maisRecente = agendamentos.getFirst();
        Usuario cliente = maisRecente.getUsuario();
        return new ClienteHistoricoResumoOutput(
                cliente.getId(),
                cliente.getNome(),
                obterIniciais(cliente.getNome()),
                obterEstilos(agendamentos),
                agendamentos.size(),
                somarValores(agendamentos),
                maisRecente.getDataHora()
        );
    }

    private SessaoHistoricoOutput mapearSessao(Agendamento agendamento) {
        Orcamento orcamento = agendamento.getOrcamento();
        return new SessaoHistoricoOutput(
                agendamento.getId(),
                agendamento.getDataHora(),
                orcamento == null ? null : orcamento.getIdeia(),
                obterEstilo(orcamento),
                obterDuracaoEmMinutos(agendamento, orcamento),
                orcamento == null ? null : orcamento.getValor()
        );
    }

    private Integer obterDuracaoEmMinutos(Agendamento agendamento, Orcamento orcamento) {
        if (agendamento.getTempoDuracao() != null) {
            return agendamento.getTempoDuracao();
        }
        Time tempo = orcamento == null ? null : orcamento.getTempo();
        if (tempo == null) {
            return null;
        }
        return tempo.toLocalTime().toSecondOfDay() / 60;
    }

    private double somarValores(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .map(Agendamento::getOrcamento)
                .filter(Objects::nonNull)
                .map(Orcamento::getValor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private List<String> obterEstilos(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .map(Agendamento::getOrcamento)
                .filter(Objects::nonNull)
                .map(Orcamento::getEstilo)
                .filter(estilo -> estilo != null && !estilo.isBlank())
                .map(String::trim)
                .collect(Collectors.toMap(
                        this::normalizar,
                        estilo -> estilo,
                        (primeiro, ignorado) -> primeiro,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
    }

    private String obterEstilo(Orcamento orcamento) {
        if (orcamento == null || orcamento.getEstilo() == null || orcamento.getEstilo().isBlank()) {
            return ESTILO_NAO_INFORMADO;
        }
        return orcamento.getEstilo().trim();
    }

    private boolean correspondeABusca(ClienteHistoricoResumoOutput cliente, String termo) {
        if (termo.isBlank()) {
            return true;
        }
        String conteudo = cliente.nome() + " " + String.join(" ", cliente.estilos());
        return normalizar(conteudo).contains(termo);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcentos.toLowerCase(Locale.ROOT);
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "?";
        }
        String[] partes = nome.trim().split("\\s+");
        String primeira = obterInicial(partes[0]);
        String ultima = partes.length > 1 ? obterInicial(partes[partes.length - 1]) : "";
        return primeira + ultima;
    }

    private String obterInicial(String parteDoNome) {
        String parteNormalizada = normalizar(parteDoNome);
        return parteNormalizada.isEmpty()
                ? ""
                : parteNormalizada.substring(0, 1).toUpperCase(Locale.ROOT);
    }
}
