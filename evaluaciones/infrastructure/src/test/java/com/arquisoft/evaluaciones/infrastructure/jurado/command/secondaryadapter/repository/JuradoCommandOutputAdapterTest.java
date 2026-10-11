package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JuradoCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JuradoCommandRepository repository;

    private JuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JuradoCommandOutputAdapter(repository);
    }

    @Test
    void debeDevolverSoloLosVigentesDelConjunto_cuandoHayRemovidosYDesconocidos() {
        // Arrange
        var vigente = sembrarJurado(null);
        var removido = sembrarJurado(Instant.now());
        var sinSembrar = UUID.randomUUID();
        var otroVigenteFueraDelConjunto = sembrarJurado(null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var vigentes = adapter.consultarVigentesPorIds(Set.of(vigente, removido, sinSembrar));

        // Assert
        assertThat(vigentes).containsExactly(vigente).doesNotContain(otroVigenteFueraDelConjunto);
    }

    private UUID sembrarJurado(Instant eliminadoEn) {
        var id = UUID.randomUUID();
        entityManager.persist(JuradoJpaEntity.builder()
                .id(id)
                .identificador("ID-" + id.toString().substring(0, 8))
                .nombre("Jurado")
                .email("jurado@uco.edu.co")
                .eliminadoEn(eliminadoEn)
                .build());
        return id;
    }
}
