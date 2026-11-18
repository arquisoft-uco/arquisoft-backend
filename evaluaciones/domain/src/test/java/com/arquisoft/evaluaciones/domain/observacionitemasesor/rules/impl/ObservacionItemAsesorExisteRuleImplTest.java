package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ObservacionItemAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaObservacionItemAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemAsesorExisteRuleImplTest {

    private final ObservacionItemAsesorExisteRuleImpl regla = new ObservacionItemAsesorExisteRuleImpl();

    @Test
    void noDebeLanzar_cuandoExiste() {
        // Arrange
        var entrada = new ExistenciaObservacionItemAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaObservacionItemAsesor(observacion, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionItemAsesorNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.NO_ENCONTRADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(observacion.toString());
                });
    }
}
