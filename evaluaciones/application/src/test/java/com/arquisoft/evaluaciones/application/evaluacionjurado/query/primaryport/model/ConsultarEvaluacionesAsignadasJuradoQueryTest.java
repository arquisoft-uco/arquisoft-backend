package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesAsignadasJuradoQueryTest {

    private static final ConsultaCriteriaQuery CRITERIO = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

    @Test
    void debeCrear_cuandoElJuradoEsValido() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado, CRITERIO);

        // Assert
        assertThat(query.jurado()).isEqualTo(jurado);
        assertThat(query.criterio()).isSameAs(CRITERIO);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElJuradoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesAsignadasJuradoQuery.crear(null, CRITERIO))
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
