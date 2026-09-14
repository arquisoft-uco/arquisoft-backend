package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoPropiedadJuradoRuleImplTest {

    private final EvaluacionJuradoPropiedadJuradoRuleImpl regla = new EvaluacionJuradoPropiedadJuradoRuleImpl();

    @Test
    void debePermitirFlujo_cuandoActorEsElPropietario() {
        // Arrange
        UUID actor = UUID.randomUUID();
        var propiedad = new PropiedadEvaluacionJurado(actor, actor);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoActorNoEsElPropietario() {
        // Arrange
        var propiedad = new PropiedadEvaluacionJurado(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOfSatisfying(EvaluacionJuradoNoPerteneceJuradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_PERTENECE_JURADO));
    }

    @Test
    void debeLanzarExcepcion_cuandoPropietarioEsNulo() {
        // Arrange
        var propiedad = new PropiedadEvaluacionJurado(UUID.randomUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
    }
}
