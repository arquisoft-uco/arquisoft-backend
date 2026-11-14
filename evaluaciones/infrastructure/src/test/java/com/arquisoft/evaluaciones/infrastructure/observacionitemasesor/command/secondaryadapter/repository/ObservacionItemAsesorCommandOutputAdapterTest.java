package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.entity.ObservacionItemAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ObservacionItemAsesorCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionItemAsesorCommandRepository repository;

    private AppLogger logger;

    private ObservacionItemAsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        logger = mock(AppLogger.class);
        adapter = new ObservacionItemAsesorCommandOutputAdapter(repository, logger);
    }

    @Test
    void debePersistirLaObservacion_cuandoRegistrar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new ObservacionItemAsesorEntity(id, evaluacion, "Sustento"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var persistida = entityManager.find(ObservacionItemAsesorJpaEntity.class, id);
        assertThat(persistida).isNotNull();
        assertThat(persistida.getEvaluacionCuantitativaAsesorId()).isEqualTo(evaluacion);
        assertThat(persistida.getDescripcion()).isEqualTo("Sustento");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeDetectarSoloLaMismaEvaluacionYDescripcion_cuandoConsultaExistencia() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionItemAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionCuantitativaAsesorId(evaluacion)
                .descripcion("Sustento")
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var existe = adapter.existePorEvaluacionYDescripcion(evaluacion, "Sustento");
        var otraEvaluacion = adapter.existePorEvaluacionYDescripcion(UtilUUID.generarNuevoUUID(), "Sustento");
        var otraDescripcion = adapter.existePorEvaluacionYDescripcion(evaluacion, "Otra");

        // Assert
        assertThat(existe).isTrue();
        assertThat(otraEvaluacion).isFalse();
        assertThat(otraDescripcion).isFalse();
    }

    @Test
    void debeActualizarSoloLaDescripcionDeLaObservacion_cuandoActualizar() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var id = sembrar(evaluacion, "Original");
        var otra = sembrar(evaluacion, "Otra");

        // Act
        adapter.actualizarDescripcion(id, "Nueva");
        entityManager.flush();
        entityManager.clear();

        // Assert
        var actualizada = entityManager.find(ObservacionItemAsesorJpaEntity.class, id);
        assertThat(actualizada.getDescripcion()).isEqualTo("Nueva");
        assertThat(actualizada.getEvaluacionCuantitativaAsesorId()).isEqualTo(evaluacion);
        assertThat(entityManager.find(ObservacionItemAsesorJpaEntity.class, otra).getDescripcion())
                .isEqualTo("Otra");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeIgnorarLaPropiaObservacion_cuandoExisteOtraConDescripcion() {
        // Arrange
        var propia = sembrar(UtilUUID.generarNuevoUUID(), "Sustento");

        // Act
        var existe = adapter.existeOtraConDescripcion(propia, "Sustento");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeDetectarSoloOtraDeLaMismaEvaluacion_cuandoExisteOtraConDescripcion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var propia = sembrar(evaluacion, "Original");
        sembrar(evaluacion, "Repetida");
        sembrar(UtilUUID.generarNuevoUUID(), "Solo en otra evaluacion");

        // Act
        var enOtraDeLaMisma = adapter.existeOtraConDescripcion(propia, "Repetida");
        var enOtraEvaluacion = adapter.existeOtraConDescripcion(propia, "Solo en otra evaluacion");
        var distinta = adapter.existeOtraConDescripcion(propia, "Distinta");
        var observacionInexistente = adapter.existeOtraConDescripcion(UtilUUID.generarNuevoUUID(), "Repetida");

        // Assert
        assertThat(enOtraDeLaMisma).isTrue();
        assertThat(enOtraEvaluacion).isFalse();
        assertThat(distinta).isFalse();
        assertThat(observacionInexistente).isFalse();
    }

    private UUID sembrar(UUID evaluacion, String descripcion) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionItemAsesorJpaEntity.builder()
                .id(id)
                .evaluacionCuantitativaAsesorId(evaluacion)
                .descripcion(descripcion)
                .build());
        entityManager.flush();
        entityManager.clear();
        return id;
    }
}
