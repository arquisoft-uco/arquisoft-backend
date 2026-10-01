package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentantePropietarioEvaluacionRuleImplTest {

    private final RepresentantePropietarioEvaluacionRuleImpl regla =
            new RepresentantePropietarioEvaluacionRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElRepresentanteNoEsDuenoDeLaEvaluacion() {
        // Arrange
        var propiedad = new PropiedadEvaluacionFicha(UUID.randomUUID(), UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debePasar_cuandoElRepresentanteEsDuenoDeLaEvaluacion() {
        // Arrange
        var propiedad = new PropiedadEvaluacionFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }
}
