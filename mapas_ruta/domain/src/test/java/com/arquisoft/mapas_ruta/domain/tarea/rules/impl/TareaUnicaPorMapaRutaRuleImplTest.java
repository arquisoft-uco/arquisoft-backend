package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.DisponibilidadTarea;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaUnicaPorMapaRutaRuleImplTest {

    private final TareaUnicaPorMapaRutaRuleImpl rule = new TareaUnicaPorMapaRutaRuleImpl();

    @Test
    void debeLanzar_cuandoYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadTarea(UtilUUID.generarNuevoUUID(), "Tarea", true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(TareaDuplicadaException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.Tarea.TAREA_DUPLICADA);
    }

    @Test
    void noDebeLanzar_cuandoNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadTarea(UtilUUID.generarNuevoUUID(), "Tarea", false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
