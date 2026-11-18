package com.arquisoft.mapas_ruta.domain.planestimado.rules.impl;

import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoTareaFinalizadaException;
import com.arquisoft.mapas_ruta.domain.planestimado.model.EstadoTareaPlanEstimado;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaAceptaPlanEstimadoRuleImplTest {

    private static final String DESCRIPCION = "Redactar el capítulo uno";

    private final TareaAceptaPlanEstimadoRuleImpl rule = new TareaAceptaPlanEstimadoRuleImpl();

    @Test
    void debeLanzarTareaFinalizada_cuandoLaTareaEstaFinalizada() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var estado = new EstadoTareaPlanEstimado(tarea, DESCRIPCION, EstadoItem.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOfSatisfying(PlanEstimadoTareaFinalizadaException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TAREA_FINALIZADA);
                    assertThat(excepcion.getError().getMessage())
                            .contains(DESCRIPCION)
                            .doesNotContain(tarea.toString());
                    assertThat(excepcion.getMessage()).contains(tarea.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoItem.class, names = {"NUEVA", "EN_PROGRESO"})
    void noDebeLanzar_cuandoLaTareaNoEstaFinalizada(EstadoItem estadoItem) {
        // Arrange
        var estado = new EstadoTareaPlanEstimado(UtilUUID.generarNuevoUUID(), DESCRIPCION, estadoItem);

        // Act & Assert
        assertThatCode(() -> rule.validar(estado)).doesNotThrowAnyException();
    }
}
