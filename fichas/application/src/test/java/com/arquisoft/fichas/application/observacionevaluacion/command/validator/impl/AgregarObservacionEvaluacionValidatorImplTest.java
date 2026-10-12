package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarObservacionEvaluacionValidatorImplTest {

    private final AgregarObservacionEvaluacionValidatorImpl validator = new AgregarObservacionEvaluacionValidatorImpl();

    private final AgregacionObservacionEvaluacionDomain entrada = AgregacionObservacionEvaluacionDomain.crear(
            ObservacionEvaluacionDomain.crear(UtilUUID.generarNuevoUUID(), "Observación válida"),
            UtilUUID.generarNuevoUUID());

    @Test
    void debeNoLanzar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoTodasLasReglasFallan() {
        // Act & Assert — la existencia decide primero
        assertThatThrownBy(() -> validator.validar(entrada, false, false, EstadoEvaluacion.APROBADA, true))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPropia_antesQueCerradaYDuplicada_cuandoElRepresentanteNoEsPropietario() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, false, EstadoEvaluacion.APROBADA, true))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarCerrada_antesQueDuplicada_cuandoLaEvaluacionEstaEnEstadoTerminal() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.DESCARTADA, true))
                .isInstanceOf(EvaluacionFichaCerradaException.class);
    }

    @Test
    void debeLanzarDuplicada_cuandoSoloLaObservacionYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, true))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class);
    }
}
