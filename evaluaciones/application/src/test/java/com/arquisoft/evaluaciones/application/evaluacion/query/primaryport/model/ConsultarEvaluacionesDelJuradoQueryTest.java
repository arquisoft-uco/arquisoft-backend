package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesDelJuradoQueryTest {

    @Test
    void debeCrearQuery_cuandoElJuradoEsValido() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarEvaluacionesDelJuradoQuery.crear(jurado, criterio);

        // Assert
        assertThat(query.jurado()).isEqualTo(jurado);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoElJuradoEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesDelJuradoQuery.crear(null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(Tuple.tuple(
                                        EvaluacionesFields.Evaluacion.JURADO,
                                        EvaluacionesCodes.Evaluacion.JURADO_REQUERIDO)));
    }
}
