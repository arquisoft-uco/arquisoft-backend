package com.arquisoft.mapas_ruta.domain.planreal;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class RemocionPlanRealDomainTest {

    @Test
    void debeCrear_cuandoPlanRealValido() {
        // Arrange
        var planReal = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionPlanRealDomain.crear(planReal);

        // Assert
        assertThat(remocion.getPlanReal()).isEqualTo(planReal);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoPlanRealEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionPlanRealDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.PlanReal.PLAN_REAL,
                                        MapasRutaCodes.PlanReal.PLAN_REAL_REQUERIDO)));
    }
}
