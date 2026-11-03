package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class CambioCriterioEvaluacionCualitativaJuradoDomainTest {

    @Test
    void debeCrearCambio_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioCriterioEvaluacionCualitativaJuradoDomain.crear(evaluacion, jurado, criterio);

        // Assert
        assertThat(cambio.getEvaluacionCualitativaJurado()).isEqualTo(evaluacion);
        assertThat(cambio.getJurado()).isEqualTo(jurado);
        assertThat(cambio.getCriterio()).isEqualTo(criterio);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanEvaluacionJuradoYCriterio() {
        // Act & Assert
        assertThatThrownBy(() -> CambioCriterioEvaluacionCualitativaJuradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.ID,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ID_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO)));
    }
}
