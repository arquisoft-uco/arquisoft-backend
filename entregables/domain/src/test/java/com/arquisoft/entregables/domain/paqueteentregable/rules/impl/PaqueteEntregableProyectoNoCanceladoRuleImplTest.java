package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableProyectoCanceladoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.ProyectoDelPaquete;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableProyectoNoCanceladoRuleImplTest {

    private final PaqueteEntregableProyectoNoCanceladoRuleImpl rule = new PaqueteEntregableProyectoNoCanceladoRuleImpl();

    @Test
    void debeLanzar_cuandoElProyectoEstaCancelado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var entrada = new ProyectoDelPaquete(entregable, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(PaqueteEntregableProyectoCanceladoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.PROYECTO_CANCELADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }

    @Test
    void debePasar_cuandoElProyectoNoEstaCancelado() {
        // Arrange
        var entrada = new ProyectoDelPaquete(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }
}
