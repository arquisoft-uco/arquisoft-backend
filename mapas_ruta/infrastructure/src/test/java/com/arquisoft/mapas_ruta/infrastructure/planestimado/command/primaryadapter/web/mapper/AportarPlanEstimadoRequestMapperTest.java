package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AportarPlanEstimadoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AportarPlanEstimadoRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var dto = new AportarPlanEstimadoRequestDTO("  Revisar  ");

        // Act
        var command = AportarPlanEstimadoRequestMapper.toCommand(dto, plan);

        // Assert
        assertThat(command.planEstimado()).isEqualTo(plan);
        assertThat(command.descripcionEstimado()).isEqualTo("Revisar");
    }
}
