package hub.orcana.service;
import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.repository.OrcamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository repository;

    @Mock
    private GerenciadorDeArquivosService gerenciadorService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrcamentoService service;

    private CadastroOrcamentoInput entradaValidaCom(List<MultipartFile> imagens) {
        return new CadastroOrcamentoInput(
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                10.0,
                "Preto, Vermelho",
                "Braço direito",
                imagens,
                1L
        );
    }

    @Test
    @DisplayName("Deve salvar orçamento com imagens e notificar observadores")
    void deveSalvarComImagensENotificar() {
        MockMultipartFile img1 = new MockMultipartFile("img", "ref1.png", "image/png", new byte[]{1, 2});
        MockMultipartFile img2 = new MockMultipartFile("img", "ref2.jpg", "image/jpeg", new byte[]{3, 4});

        when(gerenciadorService.salvarArquivo(any(MultipartFile.class)))
                .thenAnswer(inv -> "http://cdn/" + ((MultipartFile) inv.getArgument(0)).getOriginalFilename());

        when(repository.findByCodigoOrcamento(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento salvo = service.postOrcamento(entradaValidaCom(List.of(img1, img2)));

        assertNotNull(salvo);
        assertNotNull(salvo.getCodigoOrcamento());
        assertTrue(salvo.getCodigoOrcamento().startsWith("ORC-"));
        assertEquals(12, salvo.getCodigoOrcamento().length());
        assertEquals(List.of("http://cdn/ref1.png", "http://cdn/ref2.jpg"), salvo.getImagemReferencia());

        verify(gerenciadorService, times(2)).salvarArquivo(any(MultipartFile.class));
        verify(repository, times(1)).save(any(Orcamento.class));
        verify(emailService, times(1)).updateOrcamento(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve salvar orçamento sem imagens quando lista vazia")
    void deveSalvarSemImagensListaVazia() {
        when(repository.findByCodigoOrcamento(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento salvo = service.postOrcamento(entradaValidaCom(new ArrayList<>()));

        assertNotNull(salvo);
        assertTrue(salvo.getImagemReferencia().isEmpty());

        verify(gerenciadorService, never()).salvarArquivo(any(MultipartFile.class));
        verify(emailService, times(1)).updateOrcamento(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve salvar orçamento sem imagens quando lista nula")
    void deveSalvarSemImagensListaNula() {
        CadastroOrcamentoInput entrada = new CadastroOrcamentoInput(
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                10.0,
                "Preto, Vermelho",
                "Braço direito",
                null,
                1L
        );

        when(repository.findByCodigoOrcamento(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento salvo = service.postOrcamento(entrada);

        assertNotNull(salvo);
        assertNotNull(salvo.getImagemReferencia());
        assertTrue(salvo.getImagemReferencia().isEmpty());

        verify(gerenciadorService, never()).salvarArquivo(any(MultipartFile.class));
        verify(emailService, times(1)).updateOrcamento(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve gerar código ORC- único e tentar novamente em caso de colisão")
    void deveGerarCodigoUnicoComRetryEmColisao() {
        AtomicInteger chamada = new AtomicInteger(0);
        when(repository.findByCodigoOrcamento(anyString()))
                .thenAnswer(inv -> chamada.getAndIncrement() == 0
                        ? Optional.of(new Orcamento("ORC-COLISAO", "X", "x@x", "y", 1.0, "c", "l", List.of(), 1L))
                        : Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Orcamento salvo = service.postOrcamento(entradaValidaCom(null));

        assertNotNull(salvo.getCodigoOrcamento());
        assertTrue(salvo.getCodigoOrcamento().startsWith("ORC-"));
        assertEquals(12, salvo.getCodigoOrcamento().length());

        // 2 chamadas: 1ª retorna colisão, 2ª libera
        verify(repository, times(2)).findByCodigoOrcamento(anyString());
        verify(repository, times(1)).save(any(Orcamento.class));
    }

    @Test
    @DisplayName("Não deve propagar exceção lançada por observer ao notificar")
    void naoPropagaExcecaoObserver() {
        when(repository.findByCodigoOrcamento(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("Falha e-mail")).when(emailService).updateOrcamento(any(Orcamento.class));

        Orcamento salvo = service.postOrcamento(entradaValidaCom(null));

        assertNotNull(salvo);
        verify(emailService, times(1)).updateOrcamento(any(Orcamento.class));
    }

    @Test
    @DisplayName("findAllOrcamentos deve mapear para DetalhesOrcamentoOutput corretamente")
    void findAllMapeiaCorretamente() {
        Orcamento o = new Orcamento(
                "ORC-ABCD1234",
                "Maria",
                "maria@email.com",
                "Flor",
                7.5,
                "Rosa",
                "Antebraço",
                List.of("http://cdn/a.png", "http://cdn/b.png"),
                1L
        );

        when(repository.findAll()).thenReturn(List.of(o));

        List<DetalhesOrcamentoOutput> out = service.findAllOrcamentos();

        assertEquals(1, out.size());
        DetalhesOrcamentoOutput d = out.get(0);
        assertEquals(o.getCodigoOrcamento(), d.codigoOrcamento());
        assertEquals(o.getNome(), d.nome());
        assertEquals(o.getEmail(), d.email());
        assertEquals(o.getIdeia(), d.ideia());
        assertEquals(o.getTamanho(), d.tamanho());
        assertEquals(o.getCores(), d.cores());
        assertEquals(o.getLocalCorpo(), d.localCorpo());
        assertEquals(o.getImagemReferencia(), d.imagemReferencia());
    }

    @Test
    @DisplayName("Deve enviar para o repositório os URLs retornados pelo gerenciador de arquivos")
    void devePersistirUrlsGeradas() {
        MockMultipartFile img = new MockMultipartFile("img", "foto.png", "image/png", new byte[]{1});

        when(gerenciadorService.salvarArquivo(any(MultipartFile.class))).thenReturn("http://cdn/foto.png");
        when(repository.findByCodigoOrcamento(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Orcamento.class))).thenAnswer(inv -> inv.getArgument(0));

        service.postOrcamento(entradaValidaCom(List.of(img)));

        ArgumentCaptor<Orcamento> captor = ArgumentCaptor.forClass(Orcamento.class);
        verify(repository).save(captor.capture());

        Orcamento enviado = captor.getValue();
        assertEquals(List.of("http://cdn/foto.png"), enviado.getImagemReferencia());
    }
}
