package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.model.ExistenciaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionExisteRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionExisteRuleImplTest {

    @Test
    void noDebeLanzar_cuandoLaObservacionExiste() {
        // Arrange
        var regla = new ObservacionExisteRuleImpl();
        var entrada = new ExistenciaObservacion(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoExiste() {
        // Arrange
        var regla = new ObservacionExisteRuleImpl();
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaObservacion(observacion, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionNoEncontradaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.NO_ENCONTRADA);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(observacion.toString(), "??");
                    assertThat(ex.getMessage()).contains(observacion.toString());
                });
    }
}
