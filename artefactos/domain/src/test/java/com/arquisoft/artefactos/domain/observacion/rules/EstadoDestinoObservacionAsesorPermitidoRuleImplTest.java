package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.EstadoDestinoObservacionAsesorPermitidoRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDestinoObservacionAsesorPermitidoRuleImplTest {

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"CERRADA", "REABIERTA"})
    void noDebeLanzar_cuandoElDestinoEsCerradaOReabierta(EstadoObservacion estado) {
        // Arrange
        var regla = new EstadoDestinoObservacionAsesorPermitidoRuleImpl();
        var entrada = new DestinoCambioEstadoObservacion(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"NUEVA", "EN_PROGRESO", "SOLUCIONADA"})
    void debeLanzar_cuandoElDestinoNoEstaPermitido(EstadoObservacion estado) {
        // Arrange
        var regla = new EstadoDestinoObservacionAsesorPermitidoRuleImpl();
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new DestinoCambioEstadoObservacion(observacion, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstadoDestinoObservacionNoPermitidoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.ESTADO_DESTINO_NO_PERMITIDO);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(observacion.toString(), "??");
                    assertThat(ex.getMessage()).contains(observacion.toString(), estado.getId());
                });
    }
}
