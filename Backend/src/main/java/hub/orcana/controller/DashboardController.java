package hub.orcana.controller;

import hub.orcana.service.DashboardService; // 1. Mudar o serviço
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
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
        List<Double> faturamento = service.getFaturamentoUltimos12Meses();
        return ResponseEntity.ok(faturamento);
    }

    @GetMapping("/kpis") 
    @Operation(summary = "Retorna os principais KPIs para o dashboard")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Map<String, Object>> getDashboardKPIs() {
        Map<String, Object> kpis = service.getDashboardKPIs();
        return ResponseEntity.ok(kpis);
    }
}