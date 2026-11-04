package com.arquisoft.mapas_ruta.domain.planestimado;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class RemocionPlanEstimadoDomainTest {

    @Test
    void debeCrear_cuandoPlanEstimadoValido() {
        // Arrange
        var planEstimado = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionPlanEstimadoDomain.crear(planEstimado);

        // Assert
        assertThat(remocion.getPlanEstimado()).isEqualTo(planEstimado);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoPlanEstimadoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionPlanEstimadoDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanEstimado.PLAN_ESTIMADO,
                                        MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_REQUERIDO)));
    }
}
