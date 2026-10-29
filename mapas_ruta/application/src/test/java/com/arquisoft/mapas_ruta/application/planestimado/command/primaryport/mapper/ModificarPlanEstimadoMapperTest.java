package com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.ModificarPlanEstimadoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarPlanEstimadoMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var command = new ModificarPlanEstimadoCommand(UtilUUID.generarNuevoUUID(), "Revisar");

        // Act
        var modificacion = ModificarPlanEstimadoMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getPlanEstimado()).isEqualTo(command.planEstimado());
        assertThat(modificacion.getDescripcionEstimado()).isEqualTo("Revisar");
    }
}
