package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.entregables.domain.paqueteentregable.model.ExistenciaPaqueteResultadoEvaluacion;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteResultadoEvaluacionDisponibleRuleImplTest {

    private final PaqueteResultadoEvaluacionDisponibleRuleImpl rule =
            new PaqueteResultadoEvaluacionDisponibleRuleImpl();

    @Test
    void debeLanzar_cuandoElPaqueteEsRequeridoYNoExiste() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaPaqueteResultadoEvaluacion(entregable, true, false)))
                .isInstanceOfSatisfying(PaqueteEntregableNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.NO_DISPONIBLE);
                    assertThat(ex.getMessage()).contains(entregable.toString());
                });
    }

    @Test
    void debePermitir_cuandoElPaqueteEsRequeridoYExisteOCuandoNoEsRequerido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaPaqueteResultadoEvaluacion(entregable, true, true)))
                .doesNotThrowAnyException();
        assertThatCode(() -> rule.validar(new ExistenciaPaqueteResultadoEvaluacion(entregable, false, false)))
                .doesNotThrowAnyException();
    }
}
