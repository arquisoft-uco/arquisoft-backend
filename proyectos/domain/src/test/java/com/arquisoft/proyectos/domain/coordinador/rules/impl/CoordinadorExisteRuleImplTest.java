package com.arquisoft.proyectos.domain.coordinador.rules.impl;

import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorExisteRuleImplTest {

    private final CoordinadorExisteRuleImpl regla = new CoordinadorExisteRuleImpl();

    @Test
    void debePasar_cuandoElCoordinadorExiste() {
        // Arrange
        var existencia = new ExistenciaCoordinador(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElCoordinadorNoExiste() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaCoordinador(coordinador, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(CoordinadorNoEncontradoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(ProyectosCodes.Coordinador.NO_ENCONTRADO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(coordinador.toString());
                    assertThat(excepcion.getMessage()).contains(coordinador.toString());
                });
    }
}
