package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarEvaluacionesFichaPerfilEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosIdentificadoresNoNulos() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzar400_cuandoFichaEsNula() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(null, estudiante));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoFichaYEstudianteNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilEstudianteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::campo, ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        tuple(FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL,
                                FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA),
                        tuple(FichasFields.EvaluacionFichaPerfil.ESTUDIANTE,
                                FichasCodes.EvaluacionFichaPerfil.ESTUDIANTE_REQUERIDO));
    }
}
