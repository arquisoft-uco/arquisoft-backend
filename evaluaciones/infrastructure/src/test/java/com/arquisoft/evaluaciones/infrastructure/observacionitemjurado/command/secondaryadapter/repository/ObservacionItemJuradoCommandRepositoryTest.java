package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObservacionItemJuradoCommandRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemJuradoCommandRepository repository;

    private void sembrarObservacion(UUID evaluacionCuantitativaJurado) {
        entityManager.persist(ObservacionItemJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionCuantitativaJuradoId(evaluacionCuantitativaJurado)
                .descripcion("Sustenta el puntaje otorgado")
                .build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarTrue_cuandoAlgunaEvaluacionDelConjuntoTieneObservaciones() {
        // Arrange
        var conObservacion = UUID.randomUUID();
        var sinObservacion = UUID.randomUUID();
        sembrarObservacion(conObservacion);

        // Act
        var existen = repository.existsByEvaluacionCuantitativaJuradoIdIn(Set.of(conObservacion, sinObservacion));

        // Assert
        assertThat(existen).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoNingunaEvaluacionDelConjuntoTieneObservacionesAunqueOtrasSi() {
        // Arrange
        sembrarObservacion(UUID.randomUUID());

        // Act
        var existen = repository.existsByEvaluacionCuantitativaJuradoIdIn(Set.of(UUID.randomUUID(), UUID.randomUUID()));

        // Assert
        assertThat(existen).isFalse();
    }
}
