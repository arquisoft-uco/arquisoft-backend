package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DestituirEstudianteProyectoGradoMapperTest {

    @Test
    void debeMapearLosTresIds_cuandoCommandValido() {
        // Arrange
        var command = DestituirEstudianteProyectoGradoCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var destitucion = DestituirEstudianteProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(destitucion.getProyectoGrado()).isEqualTo(command.proyectoGrado());
        assertThat(destitucion.getEstudiante()).isEqualTo(command.estudiante());
        assertThat(destitucion.getCoordinador()).isEqualTo(command.coordinador());
    }
}
