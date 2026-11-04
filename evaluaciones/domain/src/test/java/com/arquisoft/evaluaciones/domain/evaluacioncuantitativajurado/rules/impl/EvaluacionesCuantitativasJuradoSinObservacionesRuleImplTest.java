package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoConObservacionesException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCuantitativasJuradoSinObservacionesRuleImplTest {

    private final EvaluacionesCuantitativasJuradoSinObservacionesRuleImpl regla =
            new EvaluacionesCuantitativasJuradoSinObservacionesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunaEvaluacionTieneObservaciones() {
        // Arrange
        var entrada = new ObservacionesEvaluacionesCuantitativasJurado(
                UUID.randomUUID(), Set.of(UUID.randomUUID()), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConLosIdsDelLote_cuandoAlgunaEvaluacionTieneObservaciones() {
        // Arrange
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var evaluacionJurado = UUID.randomUUID();
        var entrada = new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(primera, segunda), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCuantitativasJuradoConObservacionesException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACIONES_CON_OBSERVACIONES);
                    assertThat(exception.getMessage())
                            .contains(primera.toString(), segunda.toString(), evaluacionJurado.toString());
                });
    }
}
