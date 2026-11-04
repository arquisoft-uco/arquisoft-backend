package com.arquisoft.evaluaciones.domain.evaluacionasesor.exception;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionSinEvaluacionAsesorExceptionTest {

    @Test
    void debeNoExponerUuidEnElMensaje_cuandoSeConstruye() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var exception = new EvaluacionSinEvaluacionAsesorException(evaluacion);

        // Assert
        assertThat(exception.getError().getMessage()).doesNotContain(evaluacion.toString());
        assertThat(exception.getMessage()).contains(evaluacion.toString());
    }
}
