package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarObservacionCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarObservacionCommand.crear(observacion, asesor);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarObservacionCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(ArtefactosCodes.Observacion.ID_REQUERIDO,
                                    ArtefactosCodes.Observacion.ASESOR_REQUERIDO);
                });
    }
}
