package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescartarEvaluacionFichaCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();

        // Act
        var command = DescartarEvaluacionFichaCommand.crear(evaluacion, representante);

        // Assert
        assertThat(command.evaluacionFichaPerfil()).isEqualTo(evaluacion);
        assertThat(command.representanteComite()).isEqualTo(representante);
    }

    @Test
    void debeAcumularErrores400_cuandoEvaluacionYRepresentanteSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DescartarEvaluacionFichaCommand.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting(e -> e.campo())
                                .contains(FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                                        FichasFields.EstadoEvaluacionFicha.REPRESENTANTE_COMITE));
    }
}
