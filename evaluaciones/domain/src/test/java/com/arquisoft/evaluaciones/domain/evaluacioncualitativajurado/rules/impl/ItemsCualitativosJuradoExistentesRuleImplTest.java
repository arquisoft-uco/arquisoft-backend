package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.ItemsCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemsCualitativosJuradoExistentesRuleImplTest {

    private final ItemsCualitativosJuradoExistentesRuleImpl regla = new ItemsCualitativosJuradoExistentesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosItemsSolicitadosExisten() {
        // Arrange
        UUID item = UUID.randomUUID();
        var existencia = new ExistenciaItemsCualitativosJurado(Set.of(item), Set.of(item));

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionConFaltantes_cuandoAlgunItemSolicitadoNoExiste() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID faltante = UUID.randomUUID();
        var existencia = new ExistenciaItemsCualitativosJurado(Set.of(existente, faltante), Set.of(existente));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(ItemsCualitativosJuradoNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.ITEMS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage()).contains(faltante.toString());
                });
    }
}
