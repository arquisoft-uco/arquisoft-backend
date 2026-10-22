package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesAsignadasJuradoQueryTest {

    @Test
    void debeCrear_cuandoElJuradoEsValido() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado);

        // Assert
        assertThat(query.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElJuradoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesAsignadasJuradoQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .singleElement()
                                .satisfies(error -> {
                                    assertThat(error.campo()).isEqualTo(EvaluacionesFields.EvaluacionJurado.JURADO);
                                    assertThat(error.codigoError())
                                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO);
                                }));
    }
}
