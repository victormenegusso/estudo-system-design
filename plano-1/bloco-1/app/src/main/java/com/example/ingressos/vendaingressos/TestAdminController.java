package com.example.ingressos.vendaingressos;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test-admin/ingressos")
public class TestAdminController {

    private static final Logger log = LoggerFactory.getLogger(TestAdminController.class);
    private final IngressoRepository ingressoRepository;

    public TestAdminController(IngressoRepository ingressoRepository) {
        this.ingressoRepository = ingressoRepository;
    }

    @PostMapping("/bulk")
    @Transactional
    public ResponseEntity<String> criarIngressos(
            @RequestParam(defaultValue = "10000") int quantidade) {

        log.info("Iniciando criacao de {} ingressos...", quantidade);
        long start = System.currentTimeMillis();

        List<Ingresso> ingressos = new ArrayList<>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            ingressos.add(new Ingresso("DISPONIVEL", "GERAL"));
        }

        ingressoRepository.saveAll(ingressos);

        long end = System.currentTimeMillis();
        log.info("Processo concluido em {} ms.", (end - start));

        return ResponseEntity.ok("Criados " + quantidade + " ingressos em " + (end - start) + "ms.");
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<String> limparIngressos() {
        log.info("Limpando a tabela de ingressos (TRUNCATE)...");
        long start = System.currentTimeMillis();

        ingressoRepository.truncateTable();

        long end = System.currentTimeMillis();
        log.info("Tabela truncada em {} ms.", (end - start));

        return ResponseEntity.ok("Tabela limpa (TRUNCATE) em " + (end - start) + "ms.");
    }
}
