package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionEstudianteNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.EstadoDestinoObservacionEstudiantePermitidoRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDestinoObservacionEstudiantePermitidoRuleImplTest {

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"EN_PROGRESO", "SOLUCIONADA"})
    void noDebeLanzar_cuandoElDestinoEsEnProgresoOSolucionada(EstadoObservacion estado) {
        // Arrange
        var regla = new EstadoDestinoObservacionEstudiantePermitidoRuleImpl();
        var entrada = new DestinoCambioEstadoObservacion(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoObservacion.class, names = {"NUEVA", "CERRADA", "REABIERTA"})
    void debeLanzar_cuandoElDestinoNoEstaPermitido(EstadoObservacion estado) {
        // Arrange
        var regla = new EstadoDestinoObservacionEstudiantePermitidoRuleImpl();
        var observacion = UtilUUID.generarNuevoUUID();
        var entrada = new DestinoCambioEstadoObservacion(observacion, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstadoDestinoObservacionEstudianteNoPermitidoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Observacion.ESTADO_DESTINO_ESTUDIANTE_NO_PERMITIDO);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(observacion.toString(), "??");
                    assertThat(ex.getMessage()).contains(observacion.toString(), estado.getId());
                });
    }
}
