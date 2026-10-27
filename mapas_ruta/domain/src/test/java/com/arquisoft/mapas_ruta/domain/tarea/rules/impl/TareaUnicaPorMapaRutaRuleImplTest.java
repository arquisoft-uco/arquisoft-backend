package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.DisponibilidadTarea;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaUnicaPorMapaRutaRuleImplTest {

    private static final String DESCRIPCION = "Redactar el capítulo uno";

    private final TareaUnicaPorMapaRutaRuleImpl rule = new TareaUnicaPorMapaRutaRuleImpl();

    @Test
    void debeLanzar_cuandoYaExiste() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadTarea(mapaRuta, DESCRIPCION, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(TareaDuplicadaException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(MapasRutaCodes.Tarea.TAREA_DUPLICADA);
                    assertThat(excepcion.getError().getMessage())
                            .contains(DESCRIPCION)
                            .doesNotContain(mapaRuta.toString());
                    assertThat(excepcion.getMessage()).contains(mapaRuta.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadTarea(UtilUUID.generarNuevoUUID(), DESCRIPCION, false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
