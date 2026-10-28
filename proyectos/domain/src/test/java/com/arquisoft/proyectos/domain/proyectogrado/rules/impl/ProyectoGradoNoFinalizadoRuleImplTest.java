package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoNoFinalizadoRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final ProyectoGradoNoFinalizadoRuleImpl regla = new ProyectoGradoNoFinalizadoRuleImpl();

    @Test
    void debePasar_cuandoEstaEnCualquierEstadoDistintoDeFinalizado() {
        // Arrange
        var estados = List.of(EstadoProyecto.EN_PROCESO, EstadoProyecto.LISTO_PARA_REVISION,
                EstadoProyecto.ATRASADO, EstadoProyecto.VACIO);

        // Act & Assert
        for (var estado : estados) {
            var actual = new EstadoActualProyectoGrado(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, estado);
            assertThatCode(() -> regla.validar(actual)).doesNotThrowAnyException();
        }
    }

    @Test
    void debeLanzarFinalizado_cuandoEstaFinalizado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var actual = new EstadoActualProyectoGrado(proyectoGrado, TITULO_PROYECTO, EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(actual))
                .isInstanceOfSatisfying(ProyectoGradoFinalizadoException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(ProyectosCodes.ProyectoGrado.FINALIZADO);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
