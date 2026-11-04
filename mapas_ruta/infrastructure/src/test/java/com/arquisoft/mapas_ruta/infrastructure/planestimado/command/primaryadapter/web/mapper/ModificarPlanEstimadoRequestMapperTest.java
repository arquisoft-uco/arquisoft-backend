package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.ModificarPlanEstimadoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarPlanEstimadoRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var dto = new ModificarPlanEstimadoRequestDTO("  Revisar  ");

        // Act
        var command = ModificarPlanEstimadoRequestMapper.toCommand(dto, plan);

        // Assert
        assertThat(command.planEstimado()).isEqualTo(plan);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }
}
