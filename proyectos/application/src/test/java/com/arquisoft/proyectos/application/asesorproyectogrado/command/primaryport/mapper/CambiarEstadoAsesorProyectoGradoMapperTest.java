package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoAsesorProyectoGradoMapperTest {

    @Test
    void debeConstruirElObjetoDeAccion_cuandoSeMapeaElCommand() {
        // Arrange
        var command = new CambiarEstadoAsesorProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

        // Act
        var cambio = CambiarEstadoAsesorProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(cambio.getAsesorProyectoGrado()).isEqualTo(command.asesorProyectoGrado());
        assertThat(cambio.getCoordinador()).isEqualTo(command.coordinador());
        assertThat(cambio.getEstadoNuevo()).isEqualTo(EstadoAsesor.FINALIZADO);
    }
}
