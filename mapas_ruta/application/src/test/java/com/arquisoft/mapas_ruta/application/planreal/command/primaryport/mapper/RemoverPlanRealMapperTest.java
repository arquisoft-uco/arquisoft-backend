package com.arquisoft.mapas_ruta.application.planreal.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverPlanRealMapperTest {

    @Test
    void debeConvertirCommandADomain_cuandoCommandValido() {
        // Arrange
        var command = new RemoverPlanRealCommand(UtilUUID.generarNuevoUUID());

        // Act
        var remocion = RemoverPlanRealMapper.toDomain(command);

        // Assert
        assertThat(remocion.getPlanReal()).isEqualTo(command.planReal());
    }
}
