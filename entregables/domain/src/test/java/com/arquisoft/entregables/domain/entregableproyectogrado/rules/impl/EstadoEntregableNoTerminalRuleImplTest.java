package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoEntregableTerminalException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableNoTerminalRuleImplTest {

    private final EstadoEntregableNoTerminalRuleImpl rule = new EstadoEntregableNoTerminalRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CERRADO", "CANCELADO"})
    void debeLanzar_cuandoElEstadoActualEsTerminal(EstadoEntregable actual) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoEntregable(id, actual, EstadoEntregable.CANCELADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(EstadoEntregableTerminalException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.ESTADO_TERMINAL);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.EXCLUDE,
            names = {"CERRADO", "CANCELADO", "VACIO"})
    void debePasar_cuandoElEstadoActualNoEsTerminal(EstadoEntregable actual) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), actual, EstadoEntregable.CANCELADO);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }
}
