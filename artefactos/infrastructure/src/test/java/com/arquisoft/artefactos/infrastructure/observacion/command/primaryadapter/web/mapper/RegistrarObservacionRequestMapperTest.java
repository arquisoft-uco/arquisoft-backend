package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.RegistrarObservacionRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoPathBodyEIdentidadSonValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarObservacionRequestMapper.toCommand(
                revision, new RegistrarObservacionRequestDTO(" Detalle "), asesor);

        // Assert
        assertThat(command.revisionAsesor()).isEqualTo(revision);
        assertThat(command.descripcion()).isEqualTo("Detalle");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoLaDescripcionEsNula() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new RegistrarObservacionRequestDTO(null);

        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionRequestMapper.toCommand(revision, dto, asesor))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
