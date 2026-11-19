package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity.EntregableJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CoordinadorProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class CoordinadorProyectoGradoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CoordinadorProyectoGradoCommandRepository repository;

    private final AppLogger logger = mock(AppLogger.class);

    private CoordinadorProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CoordinadorProyectoGradoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeGuardarYResponderExistenciaPorProyecto_cuandoSePersisteLaReplica() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var replica = new CoordinadorProyectoGradoEntity(
                proyecto, UtilUUID.generarNuevoUUID(), "Coordinador", "coord@uco.edu.co", OCURRIDO_EN);

        // Act
        adapter.guardar(replica);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.existePorProyectoGrado(proyecto)).isTrue();
        assertThat(adapter.existePorProyectoGrado(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(CoordinadorProyectoGradoKey.LOG_GUARDADO, proyecto);
    }

    @Test
    void debeLeerElCoordinadorDelProyectoDeLaEvaluacion_cuandoLaReplicaExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableJpaEntity.builder()
                .id(entregable).proyecto("Proyecto").version(1).proyectoGrado(proyecto).build());
        entityManager.persist(EvaluacionJpaEntity.builder()
                .id(evaluacion).entregable(entregable).estadoEvaluacion("EN_PROGRESO").build());
        adapter.guardar(new CoordinadorProyectoGradoEntity(
                proyecto, coordinador, "Coordinador", "coord@uco.edu.co", OCURRIDO_EN));
        adapter.guardar(new CoordinadorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Otro", "otro@uco.edu.co", OCURRIDO_EN));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorEvaluacion(evaluacion);

        // Assert
        assertThat(encontrado).contains(new CoordinadorProyectoGradoEntity(
                proyecto, coordinador, "Coordinador", "coord@uco.edu.co", OCURRIDO_EN));
    }

    @Test
    void debeRetornarVacio_cuandoElProyectoDeLaEvaluacionNoTieneCoordinadorReplicado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableJpaEntity.builder()
                .id(entregable).proyecto("Proyecto").version(1).proyectoGrado(UtilUUID.generarNuevoUUID()).build());
        entityManager.persist(EvaluacionJpaEntity.builder()
                .id(evaluacion).entregable(entregable).estadoEvaluacion("EN_PROGRESO").build());
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.obtenerPorEvaluacion(evaluacion)).isEmpty();
    }
}
