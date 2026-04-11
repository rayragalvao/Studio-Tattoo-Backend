package hub.orcana.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerenciadorDeArquivosServiceTest {

    @InjectMocks
    private GerenciadorDeArquivosService gerenciadorService;

    @TempDir
    Path tempDir;

    private MultipartFile validFile;
    private MultipartFile emptyFile;
    private MultipartFile fileWithSpecialName;

    @BeforeEach
    void setUp() {
        // Arquivo válido para testes
        validFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "conteúdo do arquivo de teste".getBytes()
        );

        // Arquivo vazio para teste de erro
        emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // Arquivo com nome especial
        fileWithSpecialName = new MockMultipartFile(
                "file",
                "arquivo com espaços & caracteres especiais.png",
                "image/png",
                "conteúdo da imagem".getBytes()
        );
    }

    // ------------------ TESTES MÉTODO init() ------------------

    @Test
    @DisplayName("Deve criar pasta uploads quando não existe")
    void deveCriarPastaUploadsQuandoNaoExiste() throws Exception {
        // Arrange
        Path uploadsPath = tempDir.resolve("uploads");

        // Garantir que a pasta não existe
        assertFalse(Files.exists(uploadsPath));

        // Criar uma nova instância do serviço para evitar problemas com o campo final
        GerenciadorDeArquivosService testService = new GerenciadorDeArquivosService();

        // Usar reflexão para modificar o campo pastaRaiz final
        Field pastaRaizField = GerenciadorDeArquivosService.class.getDeclaredField("pastaRaiz");
        pastaRaizField.setAccessible(true);
        pastaRaizField.set(testService, uploadsPath);

        // Act
        assertDoesNotThrow(() -> testService.init());

        // Assert
        assertTrue(Files.exists(uploadsPath));
        assertTrue(Files.isDirectory(uploadsPath));
    }

    @Test
    @DisplayName("Não deve falhar quando pasta uploads já existe")
    void naoDeveFalharQuandoPastaUploadsJaExiste() throws Exception {
        // Arrange
        Path uploadsPath = tempDir.resolve("uploads");
        Files.createDirectories(uploadsPath);
        assertTrue(Files.exists(uploadsPath));

        // Criar uma nova instância do serviço
        GerenciadorDeArquivosService testService = new GerenciadorDeArquivosService();

        // Usar reflexão para modificar o campo pastaRaiz final
        Field pastaRaizField = GerenciadorDeArquivosService.class.getDeclaredField("pastaRaiz");
        pastaRaizField.setAccessible(true);
        pastaRaizField.set(testService, uploadsPath);

        // Act & Assert
        assertDoesNotThrow(() -> testService.init());

        // Pasta ainda deve existir
        assertTrue(Files.exists(uploadsPath));
    }

    // ------------------ TESTES MÉTODO salvarArquivo() ------------------

    @Test
    @DisplayName("Deve salvar arquivo com sucesso")
    void deveSalvarArquivoComSucesso() throws Exception {
        // Arrange - usar um diretório temporário real
        Path realTempDir = Files.createTempDirectory("test-save-success");

        try {
            // Criar nova instância do serviço
            GerenciadorDeArquivosService testService = new GerenciadorDeArquivosService();

            // Usar reflexão para modificar o campo pastaRaiz
            java.lang.reflect.Field pastaRaizField = GerenciadorDeArquivosService.class.getDeclaredField("pastaRaiz");
            pastaRaizField.setAccessible(true);
            pastaRaizField.set(testService, realTempDir);

            // Act
            String caminhoSalvo = testService.salvarArquivo(validFile);

            // Assert
            assertNotNull(caminhoSalvo);
            assertTrue(caminhoSalvo.contains("test-image.jpg"));

            // Verificar se arquivo foi realmente salvo
            Path arquivoSalvo = Paths.get(caminhoSalvo);
            assertTrue(Files.exists(arquivoSalvo));

            // Verificar conteúdo
            String conteudo = Files.readString(arquivoSalvo);
            assertEquals("conteúdo do arquivo de teste", conteudo);

            // Verificar que o nome do arquivo contém UUID (formato esperado: UUID_nomeOriginal)
            String nomeArquivo = arquivoSalvo.getFileName().toString();
            assertTrue(nomeArquivo.matches("^[a-f0-9-]+_test-image\\.jpg$"),
                      "Nome do arquivo deve seguir padrão UUID_nomeOriginal: " + nomeArquivo);

        } finally {
            // Limpeza - remover diretório temporário e seus arquivos
            if (Files.exists(realTempDir)) {
                try (var walk = Files.walk(realTempDir)) {
                    walk.sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Ignorar erros de limpeza
                            }
                        });
                }
            }
        }
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para arquivo vazio")
    void deveLancarRuntimeExceptionParaArquivoVazio() {
        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> gerenciadorService.salvarArquivo(emptyFile)
        );

        assertEquals("Falha ao salvar arquivo vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falhar ao escrever arquivo")
    void deveLancarRuntimeExceptionQuandoFalharAoEscreverArquivo() throws IOException {
        // Arrange
        MultipartFile arquivoProblematico = mock(MultipartFile.class);
        when(arquivoProblematico.isEmpty()).thenReturn(false);
        when(arquivoProblematico.getOriginalFilename()).thenReturn("test.txt");
        when(arquivoProblematico.getInputStream()).thenThrow(new IOException("Erro de I/O"));

        Path uploadsPath = tempDir.resolve("uploads");
        Files.createDirectories(uploadsPath);

        try (MockedStatic<Path> mockedPath = mockStatic(Path.class)) {
            mockedPath.when(() -> Path.of("uploads")).thenReturn(uploadsPath);

            // Act & Assert
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> gerenciadorService.salvarArquivo(arquivoProblematico)
            );

            assertEquals("Falha ao salvar o arquivo.", exception.getMessage());
            assertTrue(exception.getCause() instanceof IOException);
        }
    }

    @Test
    @DisplayName("Deve gerar nomes únicos para arquivos diferentes")
    void deveGerarNomesUnicosParaArquivosDiferentes() throws Exception {
        // Arrange - usar um diretório temporário real
        Path realTempDir = Files.createTempDirectory("test-uploads");

        try {
            // Criar nova instância do serviço
            GerenciadorDeArquivosService testService = new GerenciadorDeArquivosService();

            // Usar reflexão para modificar o campo pastaRaiz
            java.lang.reflect.Field pastaRaizField = GerenciadorDeArquivosService.class.getDeclaredField("pastaRaiz");
            pastaRaizField.setAccessible(true);

            // Para contornar o modificador final, usar setAccessible e definir diretamente
            pastaRaizField.set(testService, realTempDir);

            MultipartFile arquivo1 = new MockMultipartFile(
                    "file1", "documento.pdf", "application/pdf", "conteúdo 1".getBytes()
            );

            MultipartFile arquivo2 = new MockMultipartFile(
                    "file2", "documento.pdf", "application/pdf", "conteúdo 2".getBytes()
            );

            // Act
            String caminho1 = testService.salvarArquivo(arquivo1);
            String caminho2 = testService.salvarArquivo(arquivo2);

            // Assert - Verificações básicas
            assertNotNull(caminho1, "Caminho 1 não deve ser null");
            assertNotNull(caminho2, "Caminho 2 não deve ser null");
            assertNotEquals(caminho1, caminho2, "Os caminhos devem ser únicos");

            // Verificar que arquivos foram criados
            assertTrue(Files.exists(Paths.get(caminho1)), "Arquivo 1 deve existir");
            assertTrue(Files.exists(Paths.get(caminho2)), "Arquivo 2 deve existir");

            // Verificar que conteúdos são diferentes
            String conteudo1 = Files.readString(Paths.get(caminho1));
            String conteudo2 = Files.readString(Paths.get(caminho2));
            assertNotEquals(conteudo1, conteudo2, "Conteúdos devem ser diferentes");
            assertEquals("conteúdo 1", conteudo1);
            assertEquals("conteúdo 2", conteudo2);

        } finally {
            // Limpeza - remover diretório temporário e seus arquivos
            if (Files.exists(realTempDir)) {
                try (var walk = Files.walk(realTempDir)) {
                    walk.sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Ignorar erros de limpeza
                            }
                        });
                }
            }
        }
    }

    @Test
    @DisplayName("Deve tratar arquivo com nome null")
    void deveTratarArquivoComNomeNull() throws Exception {
        // Arrange - usar um diretório temporário real
        Path realTempDir = Files.createTempDirectory("test-null-name");

        try {
            // Criar nova instância do serviço
            GerenciadorDeArquivosService testService = new GerenciadorDeArquivosService();

            // Usar reflexão para modificar o campo pastaRaiz
            java.lang.reflect.Field pastaRaizField = GerenciadorDeArquivosService.class.getDeclaredField("pastaRaiz");
            pastaRaizField.setAccessible(true);
            pastaRaizField.set(testService, realTempDir);

            MultipartFile arquivoSemNome = new MockMultipartFile(
                    "file", null, "text/plain", "conteúdo".getBytes()
            );

            // Act
            String caminhoSalvo = testService.salvarArquivo(arquivoSemNome);

            // Assert
            assertNotNull(caminhoSalvo, "Caminho salvo não deve ser null");
            assertTrue(Files.exists(Paths.get(caminhoSalvo)), "Arquivo deve existir no caminho retornado");

            // Verificar conteúdo
            String conteudo = Files.readString(Paths.get(caminhoSalvo));
            assertEquals("conteúdo", conteudo, "Conteúdo deve ser preservado");

            // Verificar que o arquivo foi salvo - o nome exato pode variar dependendo de como o MockMultipartFile trata null
            Path arquivoSalvo = Paths.get(caminhoSalvo);
            String nomeArquivo = arquivoSalvo.getFileName().toString();

            // O nome deve ser um UUID seguido de underscore (e possivelmente string vazia após underscore)
            assertTrue(nomeArquivo.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}_.*$"),
                      "Nome do arquivo deve seguir padrão UUID_... : " + nomeArquivo);

            // Debug: vamos ver exatamente o que está sendo gerado
            System.out.println("Nome do arquivo gerado com originalFilename=null: '" + nomeArquivo + "'");

        } finally {
            // Limpeza - remover diretório temporário e seus arquivos
            if (Files.exists(realTempDir)) {
                try (var walk = Files.walk(realTempDir)) {
                    walk.sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Ignorar erros de limpeza
                            }
                        });
                }
            }
        }
    }
}

