package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoTerminalException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoUltimoEntregable;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UltimoEntregableTerminalRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final UltimoEntregableTerminalRuleImpl regla = new UltimoEntregableTerminalRuleImpl();

    @Test
    void debePasar_cuandoElUltimoEstaCerradoOCancelado() {
        // Arrange
        var estados = List.of(EstadoEntregable.CERRADO, EstadoEntregable.CANCELADO);

        // Act & Assert
        for (var estado : estados) {
            var ultimo = new EstadoUltimoEntregable(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, 2, estado);
            assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
        }
    }

    @Test
    void debeLanzar_cuandoElUltimoEstaDisponibleEvaluacion() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ultimo = new EstadoUltimoEntregable(proyectoGrado, TITULO_PROYECTO, 3,
                EstadoEntregable.DISPONIBLE_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(ultimo))
                .isInstanceOfSatisfying(UltimoEntregableNoTerminalException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(ProyectosCodes.ProyectoGrado.ULTIMO_ENTREGABLE_NO_TERMINAL);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO, "3", EstadoEntregable.DISPONIBLE_EVALUACION.getNombre())
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
