package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EstadoEntregableProyectoGradoAccesoQueryOutputAdapter.class)
class EstadoEntregableProyectoGradoAccesoQueryOutputAdapterTest {

    private static final Instant FECHA_BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private EstadoEntregableProyectoGradoAccesoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private UUID sembrar(UUID entregable, String estado, Instant fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstadoEntregableProyectoGradoJpaEntity(id, entregable, estado, fecha));
        return id;
    }

    @Test
    void debeRetornarLaMasReciente_cuandoHayVarias() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        sembrar(entregable, "DISPONIBLE_EVALUACION", FECHA_BASE);
        var reciente = sembrar(entregable, "CANCELADO", FECHA_BASE.plusSeconds(90));
        sembrar(entregable, "EN_EVALUACION", FECHA_BASE.plusSeconds(30));
        sembrar(UtilUUID.generarNuevoUUID(), "CERRADO", FECHA_BASE.plusSeconds(900));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstadoActual(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(estado -> {
            assertThat(estado.id()).isEqualTo(reciente);
            assertThat(estado.entregableProyectoGrado()).isEqualTo(entregable);
            assertThat(estado.estadoEntregable()).isEqualTo("CANCELADO");
            assertThat(estado.fechaActualizacion()).isEqualTo(FECHA_BASE.plusSeconds(90));
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
