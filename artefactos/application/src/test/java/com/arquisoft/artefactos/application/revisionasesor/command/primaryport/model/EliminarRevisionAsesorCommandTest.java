package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRevisionAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarRevisionAsesorCommand.crear(revision, asesor);

        // Assert
        assertThat(command.revisionAsesor()).isEqualTo(revision);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoRevisionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarRevisionAsesorCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO,
                                    ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO);
                });
    }
}
