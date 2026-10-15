package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestituirEstudianteProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoIdsValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = DestituirEstudianteProyectoGradoRequestMapper.toCommand(proyectoGrado, estudiante, coordinador);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeLanzar_cuandoUnIdEsNulo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> DestituirEstudianteProyectoGradoRequestMapper.toCommand(
                proyectoGrado, estudiante, null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
