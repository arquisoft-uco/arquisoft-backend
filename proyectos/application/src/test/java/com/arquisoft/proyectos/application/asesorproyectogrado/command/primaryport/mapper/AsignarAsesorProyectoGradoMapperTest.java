package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarAsesorProyectoGradoMapperTest {

    @Test
    void debeConstruirElCompuestoConEstadoAsignado_cuandoSeMapeaElCommand() {
        // Arrange
        var command = new AsignarAsesorProyectoGradoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var asignacion = AsignarAsesorProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(asignacion.getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(asignacion.getCoordinador()).isEqualTo(command.coordinador());
        assertThat(asignacion.getAsesor()).isEqualTo(command.asesor());
        assertThat(asignacion.getAsesorProyectoGrado().getEstadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO);
    }
}
