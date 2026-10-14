package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionCuantitativaJuradoCommandRepositoryTest {

    @Autowired
    private EvaluacionCuantitativaJuradoCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debeRetornarTrue_cuandoItemTieneEvaluacionRegistrada() {
        // Arrange
        var item = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJuradoId(UUID.randomUUID())
                .itemId(item)
                .puntaje(300)
                .build());
        entityManager.clear();

        // Act
        var existe = repository.existsByItemId(item);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoItemNoTieneEvaluaciones() {
        // Arrange
        repository.saveAndFlush(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJuradoId(UUID.randomUUID())
                .itemId(UUID.randomUUID())
                .puntaje(300)
                .build());
        entityManager.clear();

        // Act
        var existe = repository.existsByItemId(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
