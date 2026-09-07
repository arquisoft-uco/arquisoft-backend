package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJuradoEstudiante;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoPropiedadEstudianteRuleImplTest {

    private final EvaluacionJuradoPropiedadEstudianteRuleImpl regla =
            new EvaluacionJuradoPropiedadEstudianteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoElEstudianteEsPropietario() {
        // Arrange
        var propiedad = new PropiedadEvaluacionJuradoEstudiante(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionEsAjena() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        var propiedad = new PropiedadEvaluacionJuradoEstudiante(evaluacionJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOfSatisfying(EvaluacionJuradoNoPerteneceEstudianteException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_PERTENECE));
    }
}
