package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.RemoverPlanEstimadoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverPlanEstimadoMapperTest {

    @Test
    void debeConvertirCommandADomain_cuandoCommandValido() {
        // Arrange
        var command = new RemoverPlanEstimadoCommand(UtilUUID.generarNuevoUUID());

        // Act
        var remocion = RemoverPlanEstimadoMapper.toDomain(command);

        // Assert
        assertThat(remocion.getPlanEstimado()).isEqualTo(command.planEstimado());
    }
}
