
package com.example.ingressos.vendaingressos;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompraController {

    private static final Logger log = LoggerFactory.getLogger(CompraController.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final Counter vendasSucesso;
    private final Counter vendasErro;
    private final Counter vendasEsgotado;

    // Construtor injetando o Registry para criar métricas customizadas
    public CompraController(MeterRegistry registry) {
        this.vendasSucesso = registry.counter("vendas.sucesso");
        this.vendasErro = registry.counter("vendas.erro");
        this.vendasEsgotado = registry.counter("vendas.esgotado");
    }

    @GetMapping("/disponivel")
    public boolean verificarDisponibilidade() {
        Long count = entityManager
                .createQuery("SELECT count(i) FROM Ingresso i WHERE i.status = 'DISPONIVEL'", Long.class)
                .getSingleResult();
        return count > 0;
    }

    @GetMapping("/disponivel/contagem")
    public long contarDisponiveis() {
        return entityManager
                .createQuery("SELECT count(i) FROM Ingresso i WHERE i.status = 'DISPONIVEL'", Long.class)
                .getSingleResult();
    }

    @PostMapping("/compra")
    @Transactional
    public ResponseEntity<String> comprarIngresso() {
        try {
            // SOLUÇÃO ROBUSTA COM SKIP LOCKED
            // 1. Busca UM ingresso disponível travando a linha (FOR UPDATE)
            // 2. SKIP LOCKED faz pular os que já estão travados por outras transações.
            // Isso evita fila indiana no banco e erros de deadlock/restart.
            Query query = entityManager.createNativeQuery(
                    "SELECT * FROM ingresso WHERE status = 'DISPONIVEL' LIMIT 1 FOR UPDATE SKIP LOCKED",
                    Ingresso.class);

            // getSingleResult lança exceção se não achar nada, getResultList é mais seguro
            // aqui
            var result = query.getResultList();

            if (result.isEmpty()) {
                vendasEsgotado.increment();
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Esgotado!"); // 409 Conflict é melhor
                                                                                     // semanticamente que 500
            }

            Ingresso ingresso = (Ingresso) result.get(0);
            ingresso.setStatus("COMPRADO");

            // O merge aqui é tecnicamente redundante pois o objeto já está "managed" pelo
            // contexto
            // mas mantemos por clareza. O flush/commit ocorrerá ao fim do método
            // @Transactional
            entityManager.merge(ingresso);

            vendasSucesso.increment();
            return ResponseEntity.ok(ingresso.getId().toString());

        } catch (Exception e) {
            log.error("Erro ao processar compra", e);
            vendasErro.increment();
            return ResponseEntity.internalServerError().body("Erro interno");
        }
    }
}
