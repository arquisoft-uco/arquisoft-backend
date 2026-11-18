package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.dto.AportarPlanRealRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AportarPlanRealRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var dto = new AportarPlanRealRequestDTO("  Revisar  ");

        // Act
        var command = AportarPlanRealRequestMapper.toCommand(dto, plan);

        // Assert
        assertThat(command.planReal()).isEqualTo(plan);
        assertThat(command.descripcionReal()).isEqualTo("Revisar");
    }
}
