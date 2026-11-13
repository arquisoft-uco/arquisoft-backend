package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoCancelableException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoCancelableRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final ProyectoGradoCancelableRuleImpl regla = new ProyectoGradoCancelableRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoProyecto.class, names = {"EN_PROCESO", "ATRASADO"})
    void debePermitir_cuandoEnProcesoOAtrasado(EstadoProyecto estado) {
        // Arrange
        var actual = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(actual)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoProyecto.class, names = {"FINALIZADO", "CANCELADO"})
    void debeLanzar_cuandoFinalizadoOCancelado(EstadoProyecto estado) {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var actual = new EstadoActualProyectoGrado(proyectoGrado, TITULO_PROYECTO, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(actual))
                .isInstanceOfSatisfying(ProyectoGradoNoCancelableException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(ProyectosCodes.ProyectoGrado.NO_CANCELABLE);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
