package com.arquisoft.proyectos.application.entregableproyectogrado.command.result.mapper;

import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ActualizacionEstadoEntregableProyectoGradoResultMapperTest {

    private final EntregableProyectoGradoDomain entregable = EntregableProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 1, EstadoEntregable.CANCELADO,
            Instant.parse("2026-11-01T10:00:00Z"));

    @Test
    void debeMapearActualizado_conElIdYElEstado() {
        // Act
        var resultado = ActualizacionEstadoEntregableProyectoGradoResultMapper.toResultActualizado(entregable);

        // Assert
        assertThat(resultado.entregableProyectoGrado()).isEqualTo(entregable.getId());
        assertThat(resultado.estadoEntregable()).isEqualTo("CANCELADO");
    }

    @Test
    void debeMapearDescartado_conElOcurridoEnVigente() {
        // Arrange
        var vigente = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var resultado = ActualizacionEstadoEntregableProyectoGradoResultMapper
                .toResultDescartado(entregable, vigente);

        // Assert
        assertThat(resultado.entregableProyectoGrado()).isEqualTo(entregable.getId());
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
