package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableProyectoGradoKey;
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
class EstadoEntregableProyectoGradoCommandOutputAdapterTest {

    private static final Instant FECHA_BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private EstadoEntregableProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EstadoEntregableProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoEntregableProyectoGradoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeGuardarFila_cuandoSeRegistraElEstado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrar(new EstadoEntregableProyectoGradoEntity(id, entregable, "DISPONIBLE_EVALUACION", FECHA_BASE));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(EstadoEntregableProyectoGradoJpaEntity.class, id);
        assertThat(guardada.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(guardada.getEstadoEntregable()).isEqualTo("DISPONIBLE_EVALUACION");
        assertThat(guardada.getFechaActualizacion()).isEqualTo(FECHA_BASE);
        verify(logger).debug(EstadoEntregableProyectoGradoKey.LOG_GUARDADO, id, entregable, "DISPONIBLE_EVALUACION");
    }

    @Test
    void debeRetornarLaMasReciente_cuandoHayVarias() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var reciente = UtilUUID.generarNuevoUUID();
        adapter.registrar(new EstadoEntregableProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), entregable, "DISPONIBLE_EVALUACION", FECHA_BASE));
        adapter.registrar(new EstadoEntregableProyectoGradoEntity(
                reciente, entregable, "EN_EVALUACION", FECHA_BASE.plusSeconds(60)));
        adapter.registrar(new EstadoEntregableProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), entregable, "APROBADO", FECHA_BASE.plusSeconds(30)));
        adapter.registrar(new EstadoEntregableProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), otro, "CERRADO", FECHA_BASE.plusSeconds(600)));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstadoActual(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(estado -> {
            assertThat(estado.id()).isEqualTo(reciente);
            assertThat(estado.estadoEntregable()).isEqualTo("EN_EVALUACION");
            assertThat(estado.entregableProyectoGrado()).isEqualTo(entregable);
            assertThat(estado.fechaActualizacion()).isEqualTo(FECHA_BASE.plusSeconds(60));
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHay() {
        // Act
        var resultado = adapter.obtenerEstadoActual(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
