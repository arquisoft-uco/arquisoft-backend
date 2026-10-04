package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionEvaluacionValidatorImplTest {

    private final RemoverObservacionEvaluacionValidatorImpl validator = new RemoverObservacionEvaluacionValidatorImpl();

    private final RemocionObservacionEvaluacionDomain entrada = RemocionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

    @Test
    void debeNoLanzar_cuandoExistePropiaYAbierta() {
        // Arrange
        var pertenencia = pertenencia(true, EstadoEvaluacion.EN_EVALUACION);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, pertenencia))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaPertenenciaLlegaComoCentinela() {
        // Act & Assert — la existencia decide antes que la propiedad, que con VACIO también fallaría
        assertThatThrownBy(() -> validator.validar(entrada, false, PertenenciaObservacionEvaluacion.VACIO))
                .isInstanceOf(ObservacionEvaluacionNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPropia_antesQueCerrada_cuandoElRepresentanteNoEsPropietario() {
        // Arrange
        var pertenencia = pertenencia(false, EstadoEvaluacion.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, pertenencia))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarCerrada_cuandoLaEvaluacionEstaEnEstadoTerminal() {
        // Arrange
        var pertenencia = pertenencia(true, EstadoEvaluacion.NO_APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, pertenencia))
                .isInstanceOf(EvaluacionFichaCerradaException.class);
    }

    private PertenenciaObservacionEvaluacion pertenencia(boolean esPropietario, EstadoEvaluacion ultimoEstado) {
        return new PertenenciaObservacionEvaluacion(UtilUUID.generarNuevoUUID(), esPropietario, ultimoEstado);
    }
}
