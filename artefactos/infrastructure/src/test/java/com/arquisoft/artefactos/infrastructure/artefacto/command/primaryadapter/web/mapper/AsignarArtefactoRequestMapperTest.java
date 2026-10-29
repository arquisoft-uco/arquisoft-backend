package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommandConElAsesorDelToken_cuandoElDtoEsValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new AsignarArtefactoRequestDTO(
                proyecto.toString(), version.toString(), List.of(UtilUUID.generarNuevoUUID().toString()));

        // Act
        var command = AsignarArtefactoRequestMapper.toCommand(dto, asesor);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionRepositorioArtefacto()).isEqualTo(version);
    }

    @Test
    void debeLanzarValidacion_cuandoElDtoNoTraeDatos() {
        // Arrange
        var dto = new AsignarArtefactoRequestDTO(null, null, null);
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactoRequestMapper.toCommand(dto, asesor))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
