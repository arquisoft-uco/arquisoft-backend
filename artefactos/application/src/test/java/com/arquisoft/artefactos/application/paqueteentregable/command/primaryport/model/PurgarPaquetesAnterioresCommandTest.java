package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgarPaquetesAnterioresCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var command = PurgarPaquetesAnterioresCommand.crear(id.toString(), 2);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
        assertThat(command.versionEntregable()).isEqualTo(2);
    }

    @Test
    void debeLanzar400_cuandoUuidInvalidoYVersionNula() {
        // Act & Assert
        assertThatThrownBy(() -> PurgarPaquetesAnterioresCommand.crear("no-es-uuid", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE));
    }

    @Test
    void debeLanzar400_cuandoLaVersionEsMenorAUno() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> PurgarPaquetesAnterioresCommand.crear(id, 0))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactly(ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE));
    }
}
