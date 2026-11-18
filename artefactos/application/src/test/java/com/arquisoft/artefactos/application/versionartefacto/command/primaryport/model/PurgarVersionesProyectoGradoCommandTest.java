package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgarVersionesProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoUuidValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var command = PurgarVersionesProyectoGradoCommand.crear(id.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
    }

    @Test
    void debeLanzar400_cuandoUuidInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> PurgarVersionesProyectoGradoCommand.crear("no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.ProyectoGrado.ID_REQUERIDO)));
    }
}
