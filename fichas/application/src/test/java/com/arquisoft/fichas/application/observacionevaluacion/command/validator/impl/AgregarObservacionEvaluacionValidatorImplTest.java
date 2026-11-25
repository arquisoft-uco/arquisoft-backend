package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarObservacionEvaluacionValidatorImplTest {

    private final AgregarObservacionEvaluacionValidatorImpl validator = new AgregarObservacionEvaluacionValidatorImpl();

    private static final EstadoActualFicha FICHA_DISPONIBLE = new EstadoActualFicha(
            UtilUUID.generarNuevoUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

    private final AgregacionObservacionEvaluacionDomain entrada = AgregacionObservacionEvaluacionDomain.crear(
            ObservacionEvaluacionDomain.crear(UtilUUID.generarNuevoUUID(), "Observación válida"),
            UtilUUID.generarNuevoUUID());

    @Test
    void debeNoLanzar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, false, FICHA_DISPONIBLE))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoTodasLasReglasFallan() {
        // Act & Assert — la existencia decide primero
        assertThatThrownBy(() -> validator.validar(entrada, false, false, EstadoEvaluacion.APROBADA, true, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPropia_antesQueCerradaYDuplicada_cuandoElRepresentanteNoEsPropietario() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, false, EstadoEvaluacion.APROBADA, true, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarCerrada_antesQueDuplicada_cuandoLaEvaluacionEstaEnEstadoTerminal() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.DESCARTADA, true, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaCerradaException.class);
    }

    @Test
    void debeLanzarDuplicada_cuandoSoloLaObservacionYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, true, FICHA_DISPONIBLE))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"EN_CONSTRUCCION", "DESCARTADA", "APROBADA",
            "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarFichaNoDisponible_cuandoLaFichaNoEstaDisponible(EstadoFicha estado) {
        // Arrange
        var estadoFicha = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), estado);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, false,
                estadoFicha))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }

    @Test
    void debeAplicarDuplicadaAntesQueDisponibilidad_cuandoAmbasFallan() {
        // Arrange
        var estadoFicha = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, true,
                estadoFicha))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class);
    }
}
