package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.CambiarEstadoEntregableProyectoGradoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEntregableProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoSeConvierte() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var dto = new CambiarEstadoEntregableProyectoGradoRequestDTO("CERRADO");

        // Act
        var command = CambiarEstadoEntregableProyectoGradoRequestMapper.toCommand(entregable, dto);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.estadoEntregable()).isEqualTo("CERRADO");
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoElEstadoViene_enBlanco() {
        // Arrange
        var dto = new CambiarEstadoEntregableProyectoGradoRequestDTO(" ");

        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoEntregableProyectoGradoRequestMapper
                .toCommand(UtilUUID.generarNuevoUUID(), dto))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
