package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgarPaquetesProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoElProyectoEsUnUuidValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var command = PurgarPaquetesProyectoGradoCommand.crear(id.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
    }

    @Test
    void debeLanzar400_cuandoElProyectoNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> PurgarPaquetesProyectoGradoCommand.crear("no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactly(ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO));
    }
}
