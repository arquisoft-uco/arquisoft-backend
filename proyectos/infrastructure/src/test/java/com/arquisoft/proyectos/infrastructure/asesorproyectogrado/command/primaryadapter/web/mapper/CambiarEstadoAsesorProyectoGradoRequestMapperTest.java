package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.dto.CambiarEstadoAsesorProyectoGradoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoAsesorProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoSeMapeaElDto() {
        // Arrange
        var dto = new CambiarEstadoAsesorProyectoGradoRequestDTO("FINALIZADO");
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesorProyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoAsesorProyectoGradoRequestMapper.toCommand(
                dto, proyectoGrado, asesorProyectoGrado, coordinador);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.asesorProyectoGrado()).isEqualTo(asesorProyectoGrado);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.estadoAsesor()).isEqualTo("FINALIZADO");
    }
}
