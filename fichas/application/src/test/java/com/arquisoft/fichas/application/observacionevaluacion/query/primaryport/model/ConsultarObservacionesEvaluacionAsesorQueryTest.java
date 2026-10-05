package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarObservacionesEvaluacionAsesorQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosIdentificadoresNoSonNulos() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesEvaluacionAsesorQuery.crear(evaluacionFichaPerfil, asesorFicha);

        // Assert
        assertThat(query.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEvaluacionFichaPerfilEsNula() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarObservacionesEvaluacionAsesorQuery.crear(null, asesorFicha));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoEvaluacionYAsesorFichaSonNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarObservacionesEvaluacionAsesorQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::campo, ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        tuple(FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA),
                        tuple(FichasFields.ObservacionEvaluacion.ASESOR_FICHA,
                                FichasCodes.ObservacionEvaluacion.ASESOR_FICHA_REQUERIDO));
    }
}
