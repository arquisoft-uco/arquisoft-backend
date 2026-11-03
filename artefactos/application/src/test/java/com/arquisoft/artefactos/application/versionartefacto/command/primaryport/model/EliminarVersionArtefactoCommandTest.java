package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarVersionArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarVersionArtefactoCommand.crear(version, solicitante);

        // Assert
        assertThat(command.versionArtefacto()).isEqualTo(version);
        assertThat(command.solicitante()).isEqualTo(solicitante);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarVersionArtefactoCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA,
                                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO));
    }
}
