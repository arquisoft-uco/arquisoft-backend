package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.entity.EvaluacionCualitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EvaluacionAsesorCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionAsesorCommandRepository repository;

    private final AppLogger logger = mock(AppLogger.class);

    private EvaluacionAsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeRegistrarYObtenerPorEvaluacion_cuandoSePersiste() {
        // Arrange
        var entity = new EvaluacionAsesorEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorEvaluacion(entity.evaluacion())).contains(entity);
        assertThat(adapter.obtenerPorEvaluacion(UtilUUID.generarNuevoUUID())).isEmpty();
        verify(logger).debug(EvaluacionAsesorKey.LOG_GUARDADA, entity.id());
    }

    @Test
    void debeResponderRegistros_cuandoHayCualitativaCuantitativaONinguna() {
        // Arrange
        var conCualitativa = sembrarEvaluacionAsesor();
        var conCuantitativa = sembrarEvaluacionAsesor();
        var sinRegistros = sembrarEvaluacionAsesor();
        entityManager.persist(EvaluacionCualitativaAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionAsesor(conCualitativa.getId())
                .item(UtilUUID.generarNuevoUUID())
                .criterio(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionAsesor(conCuantitativa.getId())
                .item(UtilUUID.generarNuevoUUID())
                .puntaje(4)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.existenRegistrosPorEvaluacion(conCualitativa.getEvaluacion())).isTrue();
        assertThat(adapter.existenRegistrosPorEvaluacion(conCuantitativa.getEvaluacion())).isTrue();
        assertThat(adapter.existenRegistrosPorEvaluacion(sinRegistros.getEvaluacion())).isFalse();
    }

    @Test
    void debeCambiarSoloElAsesorDeEsaFila_cuandoSeActualiza() {
        // Arrange
        var objetivo = sembrarEvaluacionAsesor();
        var otra = sembrarEvaluacionAsesor();
        var nuevoAsesor = UtilUUID.generarNuevoUUID();
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarAsesor(objetivo.getId(), nuevoAsesor);

        // Assert
        assertThat(entityManager.find(EvaluacionAsesorJpaEntity.class, objetivo.getId()).getAsesor())
                .isEqualTo(nuevoAsesor);
        assertThat(entityManager.find(EvaluacionAsesorJpaEntity.class, otra.getId()).getAsesor())
                .isEqualTo(otra.getAsesor());
        verify(logger).debug(EvaluacionAsesorKey.LOG_ASESOR_ACTUALIZADO, objetivo.getId(), nuevoAsesor);
    }

    private EvaluacionAsesorJpaEntity sembrarEvaluacionAsesor() {
        return entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacion(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
    }
}
