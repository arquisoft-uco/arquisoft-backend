package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionCualitativaJuradoCommandRepositoryTest {

    @Autowired
    private EvaluacionCualitativaJuradoCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

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

    @Test
    void debeRetornarSoloLosIdsDeLaEvaluacionJurado_cuandoUnIdPerteneceAOtraEvaluacionJurado() {
        // Arrange
        var evaluacionJuradoA = UUID.randomUUID();
        var evaluacionJuradoB = UUID.randomUUID();
        var idDeA = sembrar(evaluacionJuradoA);
        var otroIdDeA = sembrar(evaluacionJuradoA);
        var idDeB = sembrar(evaluacionJuradoB);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        var encontrados = repository.findIdsPorEvaluacionJurado(
                evaluacionJuradoA, Set.of(idDeA, idDeB, UUID.randomUUID()));

        // Assert
        assertThat(encontrados).containsExactly(idDeA);
        assertThat(encontrados).doesNotContain(otroIdDeA, idDeB);
    }

    @Test
    void debeEliminarSoloLosIdsPedidosDeLaEvaluacionJurado_cuandoUnIdPerteneceAOtraEvaluacionJurado() {
        // Arrange
        var evaluacionJuradoA = UUID.randomUUID();
        var evaluacionJuradoB = UUID.randomUUID();
        var idPedido = sembrar(evaluacionJuradoA);
        var idNoPedido = sembrar(evaluacionJuradoA);
        var idDeOtraEvaluacion = sembrar(evaluacionJuradoB);
        testEntityManager.flush();
        testEntityManager.clear();

        // Act
        repository.eliminarPorIds(evaluacionJuradoA, Set.of(idPedido, idDeOtraEvaluacion));

        // Assert
        var restantes = repository.findAll().stream()
                .map(EvaluacionCualitativaJuradoJpaEntity::getId)
                .toList();
        assertThat(restantes).containsExactlyInAnyOrder(idNoPedido, idDeOtraEvaluacion);
    }

    private UUID sembrar(UUID evaluacionJurado) {
        var entidad = EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJurado(evaluacionJurado)
                .item(UUID.randomUUID())
                .criterio(UUID.randomUUID())
                .build();
        return testEntityManager.persist(entidad).getId();
    }
}
