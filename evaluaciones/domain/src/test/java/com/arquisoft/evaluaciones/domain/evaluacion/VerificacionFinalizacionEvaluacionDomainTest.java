package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificacionFinalizacionEvaluacionDomainTest {

    @Test
    void debeCrearVerificacion_cuandoLaEvaluacionEsValida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var verificacion = VerificacionFinalizacionEvaluacionDomain.crear(evaluacion);

        // Assert
        assertThat(verificacion.getEvaluacion()).isEqualTo(evaluacion);
    }

    @Test
    void debeLanzarValidacion_cuandoLaEvaluacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> VerificacionFinalizacionEvaluacionDomain.crear(null))
                .isInstanceOf(DomainValidationException.class);
    }
}
