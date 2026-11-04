package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirArtefactoCommandTest {

    @Test
    void debeCrearCommand_cuandoArtefactoYAsesorSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = OmitirArtefactoCommand.crear(artefacto, asesor);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoArtefactoYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirArtefactoCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(e -> e.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.ID, ArtefactosFields.Artefacto.ASESOR);
                    assertThat(errores).extracting(e -> e.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.ID_REQUERIDO, ArtefactosCodes.Artefacto.ASESOR_REQUERIDO);
                });
    }
}
