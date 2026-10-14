package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionCualitativaJuradoCommandRepositoryTest {

    @Autowired
    private EvaluacionCualitativaJuradoCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void debePersistirYEncontrarItemsRegistrados_cuandoElLoteSeGuarda() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID otroItem = UUID.randomUUID();
        var entidad = EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJurado(evaluacionJurado)
                .item(item)
                .criterio(UUID.randomUUID())
                .build();

        // Act
        repository.saveAndFlush(entidad);
        entityManager.clear();
        Set<UUID> encontrados = repository.findItemsRegistrados(evaluacionJurado, Set.of(item, otroItem));

        // Assert
        assertThat(encontrados).containsExactly(item);
    }

    @Test
    void debeRetornarVacio_cuandoNingunItemSolicitadoFueRegistrado() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();

        // Act
        Set<UUID> encontrados = repository.findItemsRegistrados(evaluacionJurado, Set.of(UUID.randomUUID()));

        // Assert
        assertThat(encontrados).isEmpty();
    }

    @Test
    void debeAislarPorEvaluacionJurado_cuandoOtraEvaluacionRegistraElMismoItem() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID evaluacionJuradoA = UUID.randomUUID();
        UUID evaluacionJuradoB = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID()).evaluacionJurado(evaluacionJuradoA).item(item)
                .criterio(UUID.randomUUID()).build());
        entityManager.clear();

        // Act
        Set<UUID> encontrados = repository.findItemsRegistrados(evaluacionJuradoB, Set.of(item));

        // Assert
        assertThat(encontrados).isEmpty();
    }

    @Test
    void debeRetornarTrue_cuandoItemTieneEvaluacionRegistrada() {
        // Arrange
        var item = UUID.randomUUID();
        repository.saveAndFlush(EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID()).evaluacionJurado(UUID.randomUUID()).item(item)
                .criterio(UUID.randomUUID()).build());
        entityManager.clear();

        // Act
        var existe = repository.existsByItem(item);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoItemNoTieneEvaluaciones() {
        // Arrange
        repository.saveAndFlush(EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID()).evaluacionJurado(UUID.randomUUID()).item(UUID.randomUUID())
                .criterio(UUID.randomUUID()).build());
        entityManager.clear();

        // Act
        var existe = repository.existsByItem(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
