package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.dto.AsignarEstudiantesProyectoGradoRequestDTO;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesProyectoGradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();
        var dto = new AsignarEstudiantesProyectoGradoRequestDTO(List.of(a.toString(), b.toString()));

        // Act
        var command = AsignarEstudiantesProyectoGradoRequestMapper.toCommand(dto, proyectoGrado);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiantes()).containsExactly(a, b);
    }

    @Test
    void debeLanzar_cuandoIdNoEsUuid() {
        // Arrange
        var dto = new AsignarEstudiantesProyectoGradoRequestDTO(List.of("no-es-uuid"));
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesProyectoGradoRequestMapper.toCommand(dto, proyectoGrado))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
