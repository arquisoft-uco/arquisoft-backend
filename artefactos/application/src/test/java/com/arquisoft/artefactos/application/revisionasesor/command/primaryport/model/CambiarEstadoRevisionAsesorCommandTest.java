package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoRevisionAsesorCommandTest {

    @Test
    void debeCrear_cuandoValido() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoRevisionAsesorCommand.crear(revision, "  RESUELTA ", estudiante);

        // Assert
        assertThat(command.revisionAsesor()).isEqualTo(revision);
        assertThat(command.estado()).isEqualTo("RESUELTA");
        assertThat(command.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoRevisionNulaEstadoVacioEstudianteNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoRevisionAsesorCommand.crear(null, "   ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ESTUDIANTE);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTUDIANTE_REQUERIDO);
                    });
                });
    }
}
