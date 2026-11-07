package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class GenerarPlanRealMapperTest {

    @Test
    void debeMapearAObjetoDeAccion_cuandoCommandValido() {
        // Arrange
        var command = new GenerarPlanRealCommand(UtilUUID.generarNuevoUUID(), Instant.parse("2026-11-05T10:00:00Z"));

        // Act
        var generacion = GenerarPlanRealMapper.toDomain(command);

        // Assert
        assertThat(generacion.getTarea()).isEqualTo(command.tarea());
        assertThat(generacion.getFinalizadaEn()).isEqualTo(command.finalizadaEn());
    }
}
