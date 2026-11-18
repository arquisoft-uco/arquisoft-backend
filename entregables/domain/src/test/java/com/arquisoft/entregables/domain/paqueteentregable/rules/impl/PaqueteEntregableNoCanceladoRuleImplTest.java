package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableCanceladoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.EstadoEntregableDelPaquete;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableNoCanceladoRuleImplTest {

    private final PaqueteEntregableNoCanceladoRuleImpl rule = new PaqueteEntregableNoCanceladoRuleImpl();

    @Test
    void debeLanzar_cuandoElEntregableEstaCancelado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoEntregableDelPaquete(entregable, 2, EstadoEntregable.CANCELADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(PaqueteEntregableCanceladoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.CANCELADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELADO", "VACIO"})
    void debePasar_cuandoElEntregableNoEstaCancelado(EstadoEntregable estado) {
        // Arrange
        var entrada = new EstadoEntregableDelPaquete(UtilUUID.generarNuevoUUID(), 1, estado);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }
}
