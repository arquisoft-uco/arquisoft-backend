package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaExisteRuleImplTest {

    private final TareaExisteRuleImpl rule = new TareaExisteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaTarea(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Arrange
        var existencia = new ExistenciaTarea(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(TareaNoEncontradaException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.Tarea.TAREA_NO_ENCONTRADA);
    }
}
