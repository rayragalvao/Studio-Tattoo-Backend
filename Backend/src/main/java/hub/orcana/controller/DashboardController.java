    package hub.orcana.controller;

import hub.orcana.dto.dashboard.DashboardKPIsOutput;
import hub.orcana.dto.dashboard.DashboardOutput;
import hub.orcana.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Endpoints para dados de dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/faturamento-anual")
    @Operation(summary = "Lista o faturamento mensal dos últimos 12 meses")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<Double>> getFaturamentoAnual() {
        log.info("GET /dashboard/faturamento-anual - Iniciando busca");
        try {
            List<Double> faturamento = service.getFaturamentoUltimos12Meses();
            log.info("Faturamento retornado com sucesso: {} meses", faturamento.size());
            return ResponseEntity.ok(faturamento);
        } catch (Exception e) {
            log.error("Erro ao buscar faturamento anual", e);
            throw e;
        }
    }

    @GetMapping("/kpis")
    @Operation(summary = "Retorna os principais KPIs para o dashboard")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<DashboardOutput> getDashboardKPIs() {
        log.info("GET /dashboard/kpis - Iniciando busca");
        try {
            DashboardOutput kpis = service.getDashboardKPIs();
            log.info("KPIs retornados com sucesso");
            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            log.error("Erro ao buscar KPIs", e);
            throw e;
        }
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Retorna estatísticas gerais (totais e faturamento)")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<DashboardKPIsOutput> getEstatisticas() {
        log.info("GET /dashboard/estatisticas - Iniciando busca");
        try {
            DashboardKPIsOutput stats = service.getEstatisticas();
            log.info("Estatísticas retornadas com sucesso");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas", e);
            throw e;
        }
    }
}