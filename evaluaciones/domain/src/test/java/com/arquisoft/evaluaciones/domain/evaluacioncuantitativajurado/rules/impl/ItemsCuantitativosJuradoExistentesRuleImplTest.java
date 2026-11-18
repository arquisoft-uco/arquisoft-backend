package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.ItemsCuantitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemsCuantitativosJuradoExistentesRuleImplTest {

    private final ItemsCuantitativosJuradoExistentesRuleImpl regla = new ItemsCuantitativosJuradoExistentesRuleImpl();

    @Test
    void noDebeLanzar_cuandoTodosLosItemsExisten() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaItemsCuantitativosJurado(Set.of(item), Map.of(item, 100));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConFaltantes_cuandoAlgunItemNoExiste() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var faltante = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaItemsCuantitativosJurado(Set.of(existente, faltante), Map.of(existente, 100));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ItemsCuantitativosJuradoNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEMS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage()).contains(faltante.toString()).doesNotContain(existente.toString());
                });
    }
}
