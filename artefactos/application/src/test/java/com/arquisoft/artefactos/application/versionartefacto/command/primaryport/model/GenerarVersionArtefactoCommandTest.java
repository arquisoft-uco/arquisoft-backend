package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarVersionArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = GenerarVersionArtefactoCommand.crear(carga.toString(), artefacto, solicitante);

        // Assert
        assertThat(command.carga()).isEqualTo(carga);
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.solicitante()).isEqualTo(solicitante);
    }

    @Test
    void debeAcumularErrores400_cuandoDatosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> GenerarVersionArtefactoCommand.crear("no-es-uuid", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo").contains(
                                ArtefactosFields.VersionArtefacto.CARGA,
                                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                                ArtefactosFields.VersionArtefacto.SOLICITANTE));
    }
}
