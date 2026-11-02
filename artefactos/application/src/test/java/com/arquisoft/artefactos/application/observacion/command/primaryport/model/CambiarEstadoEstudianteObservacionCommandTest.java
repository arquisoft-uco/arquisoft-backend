package com.arquisoft.artefactos.application.observacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEstudianteObservacionCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoEstudianteObservacionCommand.crear(observacion, "  SOLUCIONADA ", estudiante);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.estado()).isEqualTo("SOLUCIONADA");
        assertThat(command.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltaAlgunDato() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoEstudianteObservacionCommand.crear(null, "  ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            ArtefactosFields.Observacion.ID,
                            ArtefactosFields.Observacion.ESTADO,
                            ArtefactosFields.Observacion.ESTUDIANTE);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            ArtefactosCodes.Observacion.ID_REQUERIDO,
                            ArtefactosCodes.Observacion.ESTADO_REQUERIDO,
                            ArtefactosCodes.Observacion.ESTUDIANTE_REQUERIDO);
                });
    }
}
