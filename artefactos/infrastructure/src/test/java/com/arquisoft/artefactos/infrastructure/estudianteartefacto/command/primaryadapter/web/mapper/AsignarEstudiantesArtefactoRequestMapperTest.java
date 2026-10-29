package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.dto.AsignarEstudiantesArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommandConArtefactoYAsesor_cuandoElDtoEsValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var dto = new AsignarEstudiantesArtefactoRequestDTO(List.of(estudiante.toString()));

        // Act
        var command = AsignarEstudiantesArtefactoRequestMapper.toCommand(artefacto, dto, asesor);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.estudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeLanzarValidacion_cuandoElDtoNoTraeEstudiantes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new AsignarEstudiantesArtefactoRequestDTO(null);

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesArtefactoRequestMapper.toCommand(artefacto, dto, asesor))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
