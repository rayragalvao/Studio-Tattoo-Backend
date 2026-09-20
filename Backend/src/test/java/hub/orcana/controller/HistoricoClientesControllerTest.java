package hub.orcana.controller;

import hub.orcana.dto.historico.DetalhesHistoricoClienteOutput;
import hub.orcana.dto.historico.HistoricoClientesOutput;
import hub.orcana.service.HistoricoClientesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoClientesControllerTest {

    @Mock
    private HistoricoClientesService service;

    @InjectMocks
    private HistoricoClientesController controller;

    @Test
    void listarDeveRetornarResumoComStatusOk() {
        HistoricoClientesOutput output = new HistoricoClientesOutput(0, 0, 0.0, List.of());
        when(service.listar("ana")).thenReturn(output);

        ResponseEntity<HistoricoClientesOutput> resposta = controller.listar("ana");

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(output, resposta.getBody());
    }

    @Test
    void detalharDeveRetornarClienteComStatusOk() {
        DetalhesHistoricoClienteOutput output = new DetalhesHistoricoClienteOutput(
                1L, "Ana", "A", List.of(), 0, 0.0, null, List.of());
        when(service.detalhar(1L)).thenReturn(output);

        ResponseEntity<DetalhesHistoricoClienteOutput> resposta = controller.detalhar(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(output, resposta.getBody());
        verify(service).detalhar(1L);
    }
}
