package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EntregableProyectoGradoCommandOutputAdapterTest {

    private static final Instant BASE = Instant.parse("2026-11-10T10:00:00Z");

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

    private UUID sembrar(UUID proyecto, int version) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(id, proyecto, version, UtilUUID.generarNuevoUUID()));
        return id;
    }

    private void sembrarEstado(UUID entregable, String estado, Instant fecha) {
        entityManager.persist(new EstadoEntregableProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), entregable, estado, fecha));
    }

    @Test
    void debeRegistrarElEntregable_cuandoSePersiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new EntregableProyectoGradoEntity(id, proyecto, 2, asesor));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(EntregableProyectoGradoJpaEntity.class, id);
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(guardado.getVersion()).isEqualTo(2);
        assertThat(guardado.getAsesor()).isEqualTo(asesor);
        verify(logger).debug(EntregableProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeIgnorarLosCanceladosYContarRechazados_cuandoConsultaSiHayEntregableVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var conVigente = UtilUUID.generarNuevoUUID();
        var conRechazado = UtilUUID.generarNuevoUUID();
        sembrarEstado(sembrar(proyecto, 1), "CANCELADO", BASE);
        sembrarEstado(sembrar(conVigente, 1), "CERRADO", BASE);
        sembrarEstado(sembrar(conRechazado, 1), "RECHAZADO", BASE);
        entityManager.flush();

        // Act
        var soloCancelados = adapter.existeVigentePorProyectoGrado(proyecto, "CANCELADO");
        var conCerrado = adapter.existeVigentePorProyectoGrado(conVigente, "CANCELADO");
        var conRechazadoVigente = adapter.existeVigentePorProyectoGrado(conRechazado, "CANCELADO");
        var sinEntregables = adapter.existeVigentePorProyectoGrado(UtilUUID.generarNuevoUUID(), "CANCELADO");

        // Assert
        assertThat(soloCancelados).isFalse();
        assertThat(conCerrado).isTrue();
        assertThat(conRechazadoVigente).isTrue();
        assertThat(sinEntregables).isFalse();
    }

    @Test
    void debeMirarSoloElUltimoEstado_cuandoElEntregableTieneHistorial() {
        // Arrange
        var cancelado = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var entregableCancelado = sembrar(cancelado, 1);
        sembrarEstado(entregableCancelado, "DISPONIBLE_EVALUACION", BASE);
        sembrarEstado(entregableCancelado, "CANCELADO", BASE.plusSeconds(60));
        var entregableVigente = sembrar(vigente, 1);
        sembrarEstado(entregableVigente, "CANCELADO", BASE);
        sembrarEstado(entregableVigente, "DISPONIBLE_EVALUACION", BASE.plusSeconds(60));
        entityManager.flush();

        // Act
        var conUltimoCancelado = adapter.existeVigentePorProyectoGrado(cancelado, "CANCELADO");
        var conUltimoVigente = adapter.existeVigentePorProyectoGrado(vigente, "CANCELADO");

        // Assert
        assertThat(conUltimoCancelado).isFalse();
        assertThat(conUltimoVigente).isTrue();
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

    @Test
    void debeObtenerPorId_cuandoElEntregableExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(id, proyecto, 2, asesor));
        entityManager.flush();
        entityManager.clear();

        // Act
        var entity = adapter.obtenerPorId(id);

        // Assert
        assertThat(entity).isPresent();
        assertThat(entity.get().proyectoGrado()).isEqualTo(proyecto);
        assertThat(entity.get().version()).isEqualTo(2);
        assertThat(entity.get().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), 1);
        entityManager.flush();

        // Act
        var entity = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(entity).isEmpty();
    }
}
