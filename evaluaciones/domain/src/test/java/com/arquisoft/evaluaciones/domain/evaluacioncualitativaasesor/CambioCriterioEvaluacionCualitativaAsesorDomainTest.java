package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioCriterioEvaluacionCualitativaAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioCriterioEvaluacionCualitativaAsesorDomain.crear(evaluacionCualitativa, asesor, criterio);

        // Assert
        assertThat(cambio.getEvaluacionCualitativaAsesor()).isEqualTo(evaluacionCualitativa);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getCriterio()).isEqualTo(criterio);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanLosTresDatos() {
        // Act & Assert
        assertThatThrownBy(() -> CambioCriterioEvaluacionCualitativaAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_REQUERIDA,
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO));
    }
}
