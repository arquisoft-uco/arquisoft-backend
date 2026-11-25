package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionEvaluacionValidatorImplTest {

    private final RemoverObservacionEvaluacionValidatorImpl validator = new RemoverObservacionEvaluacionValidatorImpl();

    private final RemocionObservacionEvaluacionDomain entrada = RemocionObservacionEvaluacionDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

    @Test
    void debeNoLanzar_cuandoExistePropiaYAbierta() {
        // Arrange
        var pertenencia = pertenencia(true, EstadoEvaluacion.EN_EVALUACION, EstadoFicha.DISPONIBLE_PARA_EVALUACION);

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
        var pertenencia = pertenencia(false, EstadoEvaluacion.APROBADA, EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, pertenencia))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarCerrada_cuandoLaEvaluacionEstaEnEstadoTerminal() {
        // Arrange
        var pertenencia = pertenencia(true, EstadoEvaluacion.NO_APROBADA, EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, pertenencia))
                .isInstanceOf(EvaluacionFichaCerradaException.class);
    }

    private PertenenciaObservacionEvaluacion pertenencia(boolean esPropietario, EstadoEvaluacion ultimoEstado,
                                                         EstadoFicha estadoFicha) {
        return new PertenenciaObservacionEvaluacion(UtilUUID.generarNuevoUUID(), esPropietario, ultimoEstado,
                UtilUUID.generarNuevoUUID(), estadoFicha);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"EN_CONSTRUCCION", "DESCARTADA", "APROBADA",
            "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarFichaNoDisponible_cuandoLaFichaNoEstaDisponible(EstadoFicha estadoFicha) {
        // Arrange
        var pertenencia = pertenencia(true, EstadoEvaluacion.EN_EVALUACION, estadoFicha);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, pertenencia))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }
}
