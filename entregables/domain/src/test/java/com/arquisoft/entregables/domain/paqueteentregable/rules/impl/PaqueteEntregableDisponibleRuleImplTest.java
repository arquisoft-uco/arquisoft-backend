package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.entregables.domain.paqueteentregable.model.DisponibilidadPaqueteEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableDisponibleRuleImplTest {

    private final PaqueteEntregableDisponibleRuleImpl rule = new PaqueteEntregableDisponibleRuleImpl();

    @Test
    void debePermitir_cuandoDisponible() {
        // Act & Assert
        assertThatCode(() -> rule.validar(new DisponibilidadPaqueteEntregable(UtilUUID.generarNuevoUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoDisponible() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new DisponibilidadPaqueteEntregable(entregable, false)))
                .isInstanceOfSatisfying(PaqueteEntregableNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                });
    }
}
