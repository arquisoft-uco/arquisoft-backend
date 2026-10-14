package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Set;
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

    private UUID sembrarEvaluacion(UUID evaluacionJurado) {
        var id = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(id)
                .evaluacionJuradoId(evaluacionJurado)
                .itemId(UUID.randomUUID())
                .puntaje(300)
                .build());
        entityManager.clear();
        return id;
    }

    @Test
    void debeRetornarSoloLosIdsDeLaEvaluacionJurado_cuandoConsultaPorEvaluacionJuradoEIds() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var propia = sembrarEvaluacion(evaluacionJurado);
        var propiaNoPedida = sembrarEvaluacion(evaluacionJurado);
        var ajena = sembrarEvaluacion(UUID.randomUUID());

        // Act
        var encontradas = repository.findIdsPorEvaluacionJurado(evaluacionJurado, Set.of(propia, ajena, UUID.randomUUID()));

        // Assert
        assertThat(encontradas).containsExactly(propia).doesNotContain(propiaNoPedida, ajena);
    }

    @Test
    void debeEliminarSoloLasPedidasDeLaEvaluacionJurado_cuandoEliminaPorIds() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var pedida = sembrarEvaluacion(evaluacionJurado);
        var propiaNoPedida = sembrarEvaluacion(evaluacionJurado);
        var ajenaPedida = sembrarEvaluacion(UUID.randomUUID());

        // Act
        repository.eliminarPorIds(evaluacionJurado, Set.of(pedida, ajenaPedida));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.existsById(pedida)).isFalse();
        assertThat(repository.existsById(propiaNoPedida)).isTrue();
        assertThat(repository.existsById(ajenaPedida)).isTrue();
    }
}
