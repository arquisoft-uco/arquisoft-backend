package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionCommandTest {

    @Test
    void debeCrear_cuandoValido() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarObservacionCommand.crear(revision, "  Revisar el marco teorico ", asesor);

        // Assert
        assertThat(command.revisionAsesor()).isEqualTo(revision);
        assertThat(command.descripcion()).isEqualTo("Revisar el marco teorico");
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoRevisionNulaAsesorNuloYDescripcionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionCommand.crear(null, "   ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.ASESOR);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.ASESOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.DESCRIPCION);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.DESCRIPCION_REQUERIDA);
                    });
                });
    }

    @Test
    void debeRechazar_cuandoDescripcionSuperaMaximoTrasRecortar() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var excedida = " " + "a".repeat(ArtefactosLimits.Observacion.DESCRIPCION_MAX + 1) + " ";

        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionCommand.crear(revision, excedida, asesor))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactly(ArtefactosCodes.Observacion.DESCRIPCION_LONGITUD_EXCEDIDA);
                });
    }
}
