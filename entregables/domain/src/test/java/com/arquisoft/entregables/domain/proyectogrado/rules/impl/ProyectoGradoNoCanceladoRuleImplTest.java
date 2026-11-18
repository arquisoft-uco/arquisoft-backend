package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoCanceladoException;
import com.arquisoft.entregables.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoNoCanceladoRuleImplTest {

    private static final String NOMBRE_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final ProyectoGradoNoCanceladoRuleImpl regla = new ProyectoGradoNoCanceladoRuleImpl();

    @Test
    void debeLanzar_cuandoCancelado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var actual = new EstadoActualProyectoGrado(proyectoGrado, NOMBRE_PROYECTO, EstadoProyecto.CANCELADO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(actual))
                .isInstanceOfSatisfying(ProyectoGradoCanceladoException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(EntregablesCodes.ProyectoGrado.CANCELADO);
                    assertThat(exception.getError().getMessage())
                            .contains(NOMBRE_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }

    @Test
    void debePasar_cuandoOtroEstado() {
        // Arrange
        var estados = List.of(EstadoProyecto.EN_PROCESO, EstadoProyecto.ATRASADO, EstadoProyecto.FINALIZADO);

        // Act & Assert
        for (var estado : estados) {
            var actual = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), NOMBRE_PROYECTO, estado);
            assertThatCode(() -> regla.validar(actual)).doesNotThrowAnyException();
        }
    }
}
