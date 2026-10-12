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

class ConsultarObservacionesEvaluacionRepresentanteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosIdentificadoresNoSonNulos() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesEvaluacionRepresentanteQuery.crear(evaluacionFichaPerfil, representanteComite);

        // Assert
        assertThat(query.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(query.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoEvaluacionYRepresentanteSonNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarObservacionesEvaluacionRepresentanteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::campo, ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        tuple(FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA),
                        tuple(FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE,
                                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO));
    }
}
