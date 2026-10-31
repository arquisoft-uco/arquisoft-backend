package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.EstadoObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;
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
class ObservacionCommandOutputAdapterTest {

    @Autowired
    private ObservacionCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ObservacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionCommandOutputAdapter(repository, logger);
        entityManager.persist(new EstadoObservacionTestEntity("NUEVA", "Nueva"));
    }

    @Test
    void debeRegistrarLaFila_yLogearElGuardado_cuandoSeRegistra() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.flush();

        // Act
        adapter.registrar(new ObservacionEntity(id, revision, "Detalle", "NUEVA"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(ObservacionJpaEntity.class, id);
        assertThat(guardada.getRevisionAsesor()).isEqualTo(revision);
        assertThat(guardada.getDescripcion()).isEqualTo("Detalle");
        assertThat(guardada.getEstado()).isEqualTo("NUEVA");
        verify(logger).debug(ObservacionKey.LOG_GUARDADA, id);
    }

    @Test
    void debeDiscriminarPorRevisionYDescripcionExacta_cuandoSeConsultaLaExistencia() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(UtilUUID.generarNuevoUUID(), revision, "Detalle", "NUEVA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var mismaDescripcion = adapter.existePorRevisionAsesorYDescripcion(revision, "Detalle");
        var otraDescripcion = adapter.existePorRevisionAsesorYDescripcion(revision, "Otro detalle");
        var otraRevision = adapter.existePorRevisionAsesorYDescripcion(UtilUUID.generarNuevoUUID(), "Detalle");

        // Assert
        assertThat(mismaDescripcion).isTrue();
        assertThat(otraDescripcion).isFalse();
        assertThat(otraRevision).isFalse();
    }

    @Test
    void debeAvanzarSoloNuevasDeLaRevision_cuandoHayOtrasRevisionesYEstados() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var otraRevision = UtilUUID.generarNuevoUUID();
        var nuevaUno = UtilUUID.generarNuevoUUID();
        var nuevaDos = UtilUUID.generarNuevoUUID();
        var yaEnProgreso = UtilUUID.generarNuevoUUID();
        var nuevaAjena = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstadoObservacionTestEntity("EN_PROGRESO", "En progreso"));
        entityManager.persist(new ObservacionTestEntity(nuevaUno, revision, "Uno", "NUEVA"));
        entityManager.persist(new ObservacionTestEntity(nuevaDos, revision, "Dos", "NUEVA"));
        entityManager.persist(new ObservacionTestEntity(yaEnProgreso, revision, "Tres", "EN_PROGRESO"));
        entityManager.persist(new ObservacionTestEntity(nuevaAjena, otraRevision, "Cuatro", "NUEVA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var cantidad = adapter.avanzarEstadoPorRevisionAsesor(revision, "NUEVA", "EN_PROGRESO");

        // Assert
        assertThat(cantidad).isEqualTo(2);
        assertThat(entityManager.find(ObservacionJpaEntity.class, nuevaUno).getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(entityManager.find(ObservacionJpaEntity.class, nuevaDos).getEstado()).isEqualTo("EN_PROGRESO");
        assertThat(entityManager.find(ObservacionJpaEntity.class, yaEnProgreso).getEstado())
                .isEqualTo("EN_PROGRESO");
        assertThat(entityManager.find(ObservacionJpaEntity.class, nuevaAjena).getEstado()).isEqualTo("NUEVA");
        verify(logger).debug(ObservacionKey.LOG_ESTADOS_ACTUALIZADOS, revision, 2);
    }

    @Test
    void debeDevolverCero_cuandoLaRevisionNoTieneObservaciones() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var cantidad = adapter.avanzarEstadoPorRevisionAsesor(revision, "NUEVA", "EN_PROGRESO");

        // Assert
        assertThat(cantidad).isZero();
    }
}
