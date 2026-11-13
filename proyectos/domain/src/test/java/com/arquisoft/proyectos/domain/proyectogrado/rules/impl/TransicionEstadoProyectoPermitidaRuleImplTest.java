package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.TransicionEstadoProyectoNoPermitidaException;
import com.arquisoft.proyectos.domain.proyectogrado.model.TransicionEstadoProyecto;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicionEstadoProyectoPermitidaRuleImplTest {

    private static final String TITULO_PROYECTO = "Plataforma de seguimiento de proyectos de grado";

    private final TransicionEstadoProyectoPermitidaRuleImpl regla = new TransicionEstadoProyectoPermitidaRuleImpl();

    static Stream<Arguments> transicionesNoPermitidas() {
        return Stream.of(
                Arguments.of(EstadoProyecto.FINALIZADO, EstadoProyecto.FINALIZADO),
                Arguments.of(EstadoProyecto.FINALIZADO, EstadoProyecto.EN_PROCESO),
                Arguments.of(EstadoProyecto.EN_PROCESO, EstadoProyecto.EN_PROCESO),
                Arguments.of(EstadoProyecto.EN_PROCESO, EstadoProyecto.ATRASADO),
                Arguments.of(EstadoProyecto.EN_PROCESO, EstadoProyecto.CANCELADO),
                Arguments.of(EstadoProyecto.ATRASADO, EstadoProyecto.CANCELADO));
    }

    @Test
    void debePasar_cuandoEnProcesoAFinalizado() {
        // Arrange
        var transicion = new TransicionEstadoProyecto(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO,
                EstadoProyecto.EN_PROCESO, EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatCode(() -> regla.validar(transicion)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoAtrasadoAFinalizado() {
        // Arrange
        var transicion = new TransicionEstadoProyecto(UtilUUID.generarNuevoUUID(), TITULO_PROYECTO,
                EstadoProyecto.ATRASADO, EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatCode(() -> regla.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("transicionesNoPermitidas")
    void debeLanzar_cuandoLaTransicionNoEsPermitida(EstadoProyecto actual, EstadoProyecto nuevo) {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoProyecto(proyectoGrado, TITULO_PROYECTO, actual, nuevo);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(transicion))
                .isInstanceOfSatisfying(TransicionEstadoProyectoNoPermitidaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(ProyectosCodes.ProyectoGrado.TRANSICION_ESTADO_NO_PERMITIDA);
                    assertThat(exception.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString());
                    assertThat(exception.getMessage()).contains(proyectoGrado.toString());
                });
    }
}
