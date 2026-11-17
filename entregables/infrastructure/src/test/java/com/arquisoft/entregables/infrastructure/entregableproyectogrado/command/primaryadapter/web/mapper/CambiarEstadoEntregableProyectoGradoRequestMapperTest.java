package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.CambiarEstadoEntregableProyectoGradoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoEntregableProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoSeConvierte() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var dto = new CambiarEstadoEntregableProyectoGradoRequestDTO("CERRADO");
        var asesor = UtilUUID.generarNuevoUUID();
        var identidad = new IdentidadActual(asesor, "a@uco.edu.co", "asesor", List.of(), "jti", null);

        // Act
        var command = CambiarEstadoEntregableProyectoGradoRequestMapper.toCommand(entregable, dto, identidad);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.estadoEntregable()).isEqualTo("CERRADO");
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
