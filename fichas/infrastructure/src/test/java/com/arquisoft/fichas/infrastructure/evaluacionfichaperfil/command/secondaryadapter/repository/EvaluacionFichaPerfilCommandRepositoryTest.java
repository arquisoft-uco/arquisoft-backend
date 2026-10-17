package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionFichaPerfilCommandRepositoryTest {

    private static final Instant T0 = Instant.parse("2026-09-01T10:00:00Z");

    @Autowired
    private EvaluacionFichaPerfilCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoEvaluacionJpaEntity enEvaluacion;
    private EstadoEvaluacionJpaEntity aprobada;
    private EstadoEvaluacionJpaEntity noAprobada;
    private EstadoEvaluacionJpaEntity descartada;
    private final UUID fichaPerfil = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        enEvaluacion = persistirCatalogo("EN_EVALUACION");
        aprobada = persistirCatalogo("APROBADA");
        noAprobada = persistirCatalogo("NO_APROBADA");
        descartada = persistirCatalogo("DESCARTADA");
    }

    @Test
    void debeDevolverListaVacia_cuandoLaFichaNoTieneEvaluaciones() {
        // Arrange
        var deOtraFicha = persistirEvaluacion(UUID.randomUUID());
        persistirEstado(UUID.randomUUID(), deOtraFicha, aprobada, T0);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).isEmpty();
    }

    @Test
    void debeContarSoloElUltimoEstadoDeCadaEvaluacion_cuandoTienenHistorial() {
        // Arrange
        var primera = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), primera, enEvaluacion, T0);
        persistirEstado(UUID.randomUUID(), primera, aprobada, T0.plusSeconds(60));
        var segunda = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), segunda, enEvaluacion, T0);
        persistirEstado(UUID.randomUUID(), segunda, aprobada, T0.plusSeconds(30));
        var tercera = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), tercera, enEvaluacion, T0);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).containsExactlyInAnyOrder(
                new ConteoEvaluacionesPorEstadoEntity("APROBADA", 2, 0),
                new ConteoEvaluacionesPorEstadoEntity("EN_EVALUACION", 1, 0));
    }

    @Test
    void debeContarLaEvaluacionUnaSolaVez_cuandoTieneVariasObservaciones() {
        // Arrange
        var evaluacion = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), evaluacion, noAprobada, T0);
        persistirObservacion(evaluacion);
        persistirObservacion(evaluacion);
        var sinObservaciones = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), sinObservaciones, noAprobada, T0);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).containsExactly(new ConteoEvaluacionesPorEstadoEntity("NO_APROBADA", 2, 1));
    }

    @Test
    void debeAgruparEnDescartadaSusObservaciones_cuandoLaEvaluacionFueDescartada() {
        // Arrange
        var descartadaConObservacion = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), descartadaConObservacion, enEvaluacion, T0);
        persistirEstado(UUID.randomUUID(), descartadaConObservacion, descartada, T0.plusSeconds(60));
        persistirObservacion(descartadaConObservacion);
        var aprobadaLimpia = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), aprobadaLimpia, aprobada, T0);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).containsExactlyInAnyOrder(
                new ConteoEvaluacionesPorEstadoEntity("DESCARTADA", 1, 1),
                new ConteoEvaluacionesPorEstadoEntity("APROBADA", 1, 0));
    }

    @Test
    void debeDesempatarPorIdMayor_cuandoDosEstadosCompartenFechaDeActualizacion() {
        // Arrange
        var evaluacion = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.fromString("00000000-0000-0000-0000-000000000001"), evaluacion, noAprobada, T0);
        persistirEstado(UUID.fromString("00000000-0000-0000-0000-000000000002"), evaluacion, aprobada, T0);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).containsExactly(new ConteoEvaluacionesPorEstadoEntity("APROBADA", 1, 0));
    }

    @Test
    void debeIgnorarEvaluacionesYObservacionesDeOtraFicha_cuandoComparteCatalogo() {
        // Arrange
        var propia = persistirEvaluacion(fichaPerfil);
        persistirEstado(UUID.randomUUID(), propia, aprobada, T0);
        var ajena = persistirEvaluacion(UUID.randomUUID());
        persistirEstado(UUID.randomUUID(), ajena, aprobada, T0);
        persistirObservacion(ajena);
        sincronizar();

        // Act
        var conteos = repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaPerfil);

        // Assert
        assertThat(conteos).containsExactly(new ConteoEvaluacionesPorEstadoEntity("APROBADA", 1, 0));
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }

    private EstadoEvaluacionJpaEntity persistirCatalogo(String id) {
        return entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(id)
                .descripcion("")
                .build());
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion(UUID ficha) {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .representanteComiteId(UUID.randomUUID())
                .fichaPerfilId(ficha)
                .fechaCreacion(T0)
                .build());
    }

    private void persistirEstado(UUID id, EvaluacionFichaPerfilJpaEntity evaluacion,
                                 EstadoEvaluacionJpaEntity estado, Instant fecha) {
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(id)
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(estado)
                .fechaActualizacion(fecha)
                .build());
    }

    private void persistirObservacion(EvaluacionFichaPerfilJpaEntity evaluacion) {
        entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionFichaPerfilId(evaluacion.getId())
                .observacion("Revisar la metodologia")
                .build());
    }
}
