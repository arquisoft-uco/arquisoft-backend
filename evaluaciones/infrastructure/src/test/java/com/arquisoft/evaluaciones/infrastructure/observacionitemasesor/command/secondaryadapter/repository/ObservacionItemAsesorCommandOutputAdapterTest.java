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
}
