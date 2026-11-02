package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEditablePorEstudianteException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionEditablePorEstudianteRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionEditablePorEstudianteRuleImplTest {

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"NUEVA", "EN_PROGRESO", "SOLUCIONADA"})
    void noDebeLanzar_cuandoLaObservacionEsEditable(EstadoObservacion estado) {
        // Arrange
        var regla = new ObservacionEditablePorEstudianteRuleImpl();
        var entrada = new EstadoActualObservacion(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"CERRADA", "REABIERTA"})
    void debeLanzar_cuandoLaObservacionNoEsEditable(EstadoObservacion estado) {
        // Arrange
        var regla = new ObservacionEditablePorEstudianteRuleImpl();
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualObservacion(observacion, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionNoEditablePorEstudianteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.NO_EDITABLE_ESTUDIANTE);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(observacion.toString(), "??");
                    assertThat(ex.getMessage()).contains(observacion.toString(), estado.getId());
                });
    }
}
