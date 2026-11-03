package com.arquisoft.evaluaciones.domain.evaluacionasesor.exception;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsesorNoPerteneceEstudianteExceptionTest {

    @Test
    void debeExponerCodigoYMensajeSinUuid_cuandoSeCrea() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var exception = new EvaluacionAsesorNoPerteneceEstudianteException(evaluacionAsesor, estudiante);

        // Assert
        assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.EvaluacionAsesor.NO_PERTENECE_ESTUDIANTE);
        assertThat(exception.getError().getMessage())
                .doesNotContain(evaluacionAsesor.toString())
                .doesNotContain(estudiante.toString());
    }

    @Test
    void debeLlevarUuidEnElDetalle_cuandoSeCrea() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var exception = new EvaluacionAsesorNoPerteneceEstudianteException(evaluacionAsesor, estudiante);

        // Assert
        assertThat(exception.getMessage()).contains(evaluacionAsesor.toString(), estudiante.toString());
    }
}
