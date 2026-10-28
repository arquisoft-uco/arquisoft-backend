package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactosRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarArtefactosRequestMapperTest {

    @Test
    void debeConstruirElCommandConElAsesorDelToken_cuandoElDtoEsValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new AsignarArtefactosRequestDTO(proyecto.toString(), List.of(version.toString()));

        // Act
        var command = AsignarArtefactosRequestMapper.toCommand(dto, asesor);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionesRepositorioArtefacto()).containsExactly(version);
    }

    @Test
    void debeLanzarValidacion_cuandoElDtoNoTraeDatos() {
        // Arrange
        var dto = new AsignarArtefactosRequestDTO(null, null);
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactosRequestMapper.toCommand(dto, asesor))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
