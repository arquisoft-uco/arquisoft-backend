package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.dto.ModificarPlanRealRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarPlanRealRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        var dto = new ModificarPlanRealRequestDTO(5, "  Revisar  ");

        // Act
        var command = ModificarPlanRealRequestMapper.toCommand(dto, plan);

        // Assert
        assertThat(command.planReal()).isEqualTo(plan);
        assertThat(command.tiempoRealDias()).isEqualTo(5);
        assertThat(command.descripcionReal()).isEqualTo("Revisar");
    }
}
