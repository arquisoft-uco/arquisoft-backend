package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EntregableProyectoGradoCommandOutputAdapterTest {

    private static final String ESTADO = "DISPONIBLE_EVALUACION";

    @Autowired
    private EntregableProyectoGradoCommandRepository entregableProyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EntregableProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoCommandOutputAdapter(entregableProyectoGradoCommandRepository, logger);
    }

    private void sembrar(UUID proyecto, int version) {
        entityManager.persist(new EntregableProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), proyecto, version, UtilUUID.generarNuevoUUID(), ESTADO));
    }

    @Test
    void debeRegistrarElEntregable_cuandoSePersiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new EntregableProyectoGradoEntity(id, proyecto, 2, asesor, ESTADO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(EntregableProyectoGradoJpaEntity.class, id);
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersion()).isEqualTo(2);
        assertThat(guardado.getAsesor()).isEqualTo(asesor);
        assertThat(guardado.getEstadoEntregable()).isEqualTo(ESTADO);
        verify(logger).debug(EntregableProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarCero_cuandoElProyectoNoTieneEntregables() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), 4);

        // Act
        var version = adapter.obtenerUltimaVersion(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(version).isZero();
    }

    @Test
    void debeRetornarLaVersionMaximaIgnorandoOtrosProyectos_cuandoHayVarios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        sembrar(proyecto, 1);
        sembrar(proyecto, 3);
        sembrar(proyecto, 2);
        sembrar(UtilUUID.generarNuevoUUID(), 9);
        entityManager.flush();

        // Act
        var version = adapter.obtenerUltimaVersion(proyecto);

        // Assert
        assertThat(version).isEqualTo(3);
    }
}
