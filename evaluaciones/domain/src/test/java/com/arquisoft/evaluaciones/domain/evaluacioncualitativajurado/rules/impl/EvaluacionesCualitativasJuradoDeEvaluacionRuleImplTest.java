package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCualitativasJuradoDeEvaluacionRuleImplTest {

    private final EvaluacionesCualitativasJuradoDeEvaluacionRuleImpl regla =
            new EvaluacionesCualitativasJuradoDeEvaluacionRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodasLasEvaluacionesSolicitadasFueronEncontradas() {
        // Arrange
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(primera, segunda), Set.of(primera, segunda));

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConLosFaltantes_cuandoAlgunaEvaluacionNoPerteneceALaEvaluacionJurado() {
        // Arrange
        var encontrada = UUID.randomUUID();
        var faltante = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(encontrada, faltante), Set.of(encontrada));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionesCualitativasJuradoNoEncontradasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACIONES_NO_ENCONTRADAS);
                    assertThat(exception.getMessage())
                            .contains(faltante.toString())
                            .doesNotContain(encontrada.toString());
                });
    }
}
