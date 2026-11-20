package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompletitudEvaluacionAsesorDomainTest {

    @Test
    void debeCrearAccion_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();

        // Act
        var completitud = CompletitudEvaluacionAsesorDomain.crear(evaluacionAsesor, true);

        // Assert
        assertThat(completitud.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(completitud.isCompleta()).isTrue();
    }

    @Test
    void debeLanzarValidacion_cuandoLaEvaluacionAsesorEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> CompletitudEvaluacionAsesorDomain.crear(null, false))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .singleElement()
                                .satisfies(error -> assertThat(error.campo())
                                        .isEqualTo(EvaluacionesFields.EvaluacionAsesor.EVALUACION_ASESOR)));
    }
}
