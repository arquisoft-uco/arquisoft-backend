package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarPuntajeEvaluacionCuantitativaAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                evaluacionCuantitativaAsesor, 350, asesor);

        // Assert
        assertThat(command.evaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(command.nuevoPuntaje()).isEqualTo(350);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAceptarPuntaje_cuandoLlegaComoDecimalSinParteFraccionaria() {
        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                UtilUUID.generarNuevoUUID(), 350.0, UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(command.nuevoPuntaje()).isEqualTo(350);
    }

    @Test
    void debeAcumularErrores_cuandoTodoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO));
    }

    @Test
    void debeFallar_cuandoPuntajeTieneParteDecimal() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                evaluacionCuantitativaAsesor, 320.7, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_NO_ENTERO));
    }

    @Test
    void debeFallar_cuandoPuntajeFueraDeRango() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                evaluacionCuantitativaAsesor, 501, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO));
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                evaluacionCuantitativaAsesor, -1, asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO));
    }
}
