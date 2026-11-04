package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoSolucionadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionSolucionadaRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionSolucionadaRuleImplTest {

    @Test
    void noDebeLanzar_cuandoLaObservacionEstaSolucionada() {
        // Arrange
        var regla = new ObservacionSolucionadaRuleImpl();
        var entrada = new EstadoActualObservacion(UtilUUID.generarNuevoUUID(), EstadoObservacion.SOLUCIONADA);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"NUEVA", "EN_PROGRESO", "CERRADA", "REABIERTA"})
    void debeLanzar_cuandoLaObservacionNoEstaSolucionada(EstadoObservacion estado) {
        // Arrange
        var regla = new ObservacionSolucionadaRuleImpl();
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualObservacion(observacion, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionNoSolucionadaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.NO_SOLUCIONADA);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(observacion.toString(), "??");
                    assertThat(ex.getMessage()).contains(observacion.toString(), estado.getId());
                });
    }
}
