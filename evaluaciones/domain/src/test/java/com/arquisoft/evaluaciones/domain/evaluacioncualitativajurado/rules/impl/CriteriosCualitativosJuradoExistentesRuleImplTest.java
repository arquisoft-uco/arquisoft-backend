package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriteriosCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriteriosCualitativosJuradoExistentesRuleImplTest {

    private final CriteriosCualitativosJuradoExistentesRuleImpl regla =
            new CriteriosCualitativosJuradoExistentesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosCriteriosSolicitadosExisten() {
        // Arrange
        UUID criterio = UUID.randomUUID();
        var existencia = new ExistenciaCriteriosCualitativosJurado(Set.of(criterio), Set.of(criterio));

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConFaltantes_cuandoAlgunCriterioSolicitadoNoExiste() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID faltante = UUID.randomUUID();
        var existencia = new ExistenciaCriteriosCualitativosJurado(Set.of(existente, faltante), Set.of(existente));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(CriteriosCualitativosJuradoNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIOS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage()).contains(faltante.toString());
                });
    }
}
