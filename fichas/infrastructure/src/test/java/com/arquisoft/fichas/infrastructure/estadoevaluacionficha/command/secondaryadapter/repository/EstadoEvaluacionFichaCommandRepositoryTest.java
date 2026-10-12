package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoEvaluacionFichaCommandRepositoryTest {

    @Autowired
    private EstadoEvaluacionFichaCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstadoEvaluacionJpaEntity enEvaluacion;
    private EstadoEvaluacionJpaEntity aprobada;
    private EvaluacionFichaPerfilJpaEntity evaluacion;
    private EvaluacionFichaPerfilJpaEntity otraEvaluacion;

    @BeforeEach
    void setUp() {
        enEvaluacion = persistirEstado("EN_EVALUACION", "En Evaluación");
        aprobada = persistirEstado("APROBADA", "Aprobada");
        evaluacion = persistirEvaluacion();
        otraEvaluacion = persistirEvaluacion();
    }

    @Test
    void debeRetornarElEstadoMasReciente_cuandoLaEvaluacionTieneVariosEstados() {
        // Arrange
        var ahora = Instant.now();
        persistirEstadoEvaluacionFicha(evaluacion, enEvaluacion, ahora.minusSeconds(60));
        var ultimo = persistirEstadoEvaluacionFicha(evaluacion, aprobada, ahora);
        persistirEstadoEvaluacionFicha(otraEvaluacion, enEvaluacion, ahora.plusSeconds(60));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = repository.findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(evaluacion.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(ultimo.getId());
        assertThat(resultado.get().getEstadoEvaluacion().getId()).isEqualTo("APROBADA");
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoTieneEstados() {
        // Arrange
        persistirEstadoEvaluacionFicha(otraEvaluacion, enEvaluacion, Instant.now());
        entityManager.flush();

        // Act
        var resultado = repository.findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(evaluacion.getId());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarTrue_cuandoLaEvaluacionYaTieneElEstado() {
        // Arrange
        persistirEstadoEvaluacionFicha(evaluacion, enEvaluacion, Instant.now());
        entityManager.flush();

        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(
                evaluacion.getId(), "EN_EVALUACION");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElEstadoPerteneceAOtraEvaluacionOEsOtroEstado() {
        // Arrange
        persistirEstadoEvaluacionFicha(otraEvaluacion, aprobada, Instant.now());
        persistirEstadoEvaluacionFicha(evaluacion, enEvaluacion, Instant.now());
        entityManager.flush();

        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(
                evaluacion.getId(), "APROBADA");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeContarSoloLosEstadosDeLaEvaluacion_cuandoHayEstadosDeOtras() {
        // Arrange
        var ahora = Instant.now();
        persistirEstadoEvaluacionFicha(evaluacion, enEvaluacion, ahora.minusSeconds(60));
        persistirEstadoEvaluacionFicha(evaluacion, aprobada, ahora);
        persistirEstadoEvaluacionFicha(otraEvaluacion, enEvaluacion, ahora);
        entityManager.flush();

        // Act
        var cantidad = repository.countByEvaluacionFichaPerfilId(evaluacion.getId());

        // Assert
        assertThat(cantidad).isEqualTo(2L);
    }

    private EstadoEvaluacionJpaEntity persistirEstado(String id, String nombre) {
        return entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("")
                .build());
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion() {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .representanteComiteId(UUID.randomUUID())
                .fichaPerfilId(UUID.randomUUID())
                .fechaCreacion(Instant.now())
                .build());
    }

    private EstadoEvaluacionFichaJpaEntity persistirEstadoEvaluacionFicha(
            EvaluacionFichaPerfilJpaEntity evaluacionFichaPerfil,
            EstadoEvaluacionJpaEntity estadoEvaluacion,
            Instant fechaActualizacion) {
        return entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionFichaPerfil(evaluacionFichaPerfil)
                .estadoEvaluacion(estadoEvaluacion)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
