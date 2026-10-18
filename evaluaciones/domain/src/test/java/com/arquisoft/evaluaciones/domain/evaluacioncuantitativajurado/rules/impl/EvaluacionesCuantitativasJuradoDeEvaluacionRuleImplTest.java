package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCuantitativasJuradoDeEvaluacionRuleImplTest {

    private final EvaluacionesCuantitativasJuradoDeEvaluacionRuleImpl regla =
            new EvaluacionesCuantitativasJuradoDeEvaluacionRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodasLasEvaluacionesSolicitadasFueronEncontradas() {
        // Arrange
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionesCuantitativasJurado(
                UUID.randomUUID(), Set.of(primera, segunda), Set.of(primera, segunda));

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConLosFaltantes_cuandoAlgunaEvaluacionNoPerteneceALaEvaluacionJurado() {
        // Arrange
        var encontrada = UUID.randomUUID();
        var faltante = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionesCuantitativasJurado(
                UUID.randomUUID(), Set.of(encontrada, faltante), Set.of(encontrada));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionesCuantitativasJuradoNoEncontradasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACIONES_NO_ENCONTRADAS);
                    assertThat(exception.getMessage())
                            .contains(faltante.toString())
                            .doesNotContain(encontrada.toString());
                });
    }
}
