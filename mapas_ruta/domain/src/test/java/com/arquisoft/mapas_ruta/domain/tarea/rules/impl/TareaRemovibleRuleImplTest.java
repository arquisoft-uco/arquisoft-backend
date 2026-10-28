package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoRemovibleException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.mapas_ruta.domain.tarea.model.RemocionTarea;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaRemovibleRuleImplTest {

    private final TareaRemovibleRuleImpl rule = new TareaRemovibleRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoItem.class, names = {"NUEVA", "EN_PROGRESO"})
    void debePasar_cuandoNuevaOEnProgreso(EstadoItem estado) {
        // Arrange
        var remocion = new RemocionTarea(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatCode(() -> rule.validar(remocion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTareaNoRemovibleException_cuandoFinalizada() {
        // Arrange
        var remocion = new RemocionTarea(UtilUUID.generarNuevoUUID(), EstadoItem.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(remocion))
                .isInstanceOf(TareaNoRemovibleException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.Tarea.TAREA_NO_REMOVIBLE);
    }
}
