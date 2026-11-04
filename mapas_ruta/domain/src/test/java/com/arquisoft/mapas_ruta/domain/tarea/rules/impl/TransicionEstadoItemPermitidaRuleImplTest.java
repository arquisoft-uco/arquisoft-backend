package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TransicionEstadoItemNoPermitidaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.mapas_ruta.domain.tarea.model.TransicionEstadoItem;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicionEstadoItemPermitidaRuleImplTest {

    private final TransicionEstadoItemPermitidaRuleImpl rule = new TransicionEstadoItemPermitidaRuleImpl();

    @Test
    void debePasar_cuandoTransicionPermitida() {
        // Arrange
        var transicion = new TransicionEstadoItem(UtilUUID.generarNuevoUUID(),
                EstadoItem.NUEVA, EstadoItem.FINALIZADA);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"EN_PROGRESO,NUEVA", "FINALIZADA,EN_PROGRESO", "EN_PROGRESO,EN_PROGRESO"})
    void debeLanzar_cuandoVuelveSaleDeFinalizadaORepite(EstadoItem actual, EstadoItem nuevo) {
        // Arrange
        var transicion = new TransicionEstadoItem(UtilUUID.generarNuevoUUID(), actual, nuevo);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOf(TransicionEstadoItemNoPermitidaException.class)
                .hasFieldOrPropertyWithValue("codigoError",
                        MapasRutaCodes.Tarea.TRANSICION_ESTADO_ITEM_NO_PERMITIDA);
    }
}
