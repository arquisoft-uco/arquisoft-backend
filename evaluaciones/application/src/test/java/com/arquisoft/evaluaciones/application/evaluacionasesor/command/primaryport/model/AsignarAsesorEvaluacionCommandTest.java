package com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarAsesorEvaluacionCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarAsesorEvaluacionCommand.crear(evaluacion, asesor.toString());

        // Assert
        assertThat(command.evaluacion()).isEqualTo(evaluacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarConFieldErrors_cuandoEvaluacionNulaYAsesorAusente() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarAsesorEvaluacionCommand.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO));
    }

    @Test
    void debeLanzarAsesorInvalido_cuandoElFormatoNoEsUuid() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarAsesorEvaluacionCommand.crear(evaluacion, "no-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactly(EvaluacionesCodes.EvaluacionAsesor.ASESOR_INVALIDO));
    }
}
