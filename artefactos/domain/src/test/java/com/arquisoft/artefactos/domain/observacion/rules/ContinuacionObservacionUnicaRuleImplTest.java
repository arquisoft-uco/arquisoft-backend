package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadContinuacionObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ContinuacionObservacionUnicaRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContinuacionObservacionUnicaRuleImplTest {

    private final ContinuacionObservacionUnicaRuleImpl regla = new ContinuacionObservacionUnicaRuleImpl();

    @Test
    void debeLanzar_cuandoReabreYLaDescripcionYaExiste() {
        // Arrange
        var entrada = new UnicidadContinuacionObservacion(
                EstadoObservacion.REABIERTA, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionDuplicadaException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.DUPLICADA));
    }

    @Test
    void noDebeLanzar_cuandoReabreSinDuplicada() {
        // Arrange
        var entrada = new UnicidadContinuacionObservacion(
                EstadoObservacion.REABIERTA, UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoCierraAunqueExistaDuplicada() {
        // Arrange
        var entrada = new UnicidadContinuacionObservacion(
                EstadoObservacion.CERRADA, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
