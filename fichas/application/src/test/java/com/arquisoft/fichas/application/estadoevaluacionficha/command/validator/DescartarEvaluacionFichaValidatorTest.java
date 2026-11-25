package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.DescartarEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionNoDescartableException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescartarEvaluacionFichaValidatorTest {

    private final DescartarEvaluacionFichaValidatorImpl validator = new DescartarEvaluacionFichaValidatorImpl();

    private final UUID evaluacion = UtilUUID.generarNuevoUUID();
    private final UUID representante = UtilUUID.generarNuevoUUID();
    private final AgregacionEstadoEvaluacionFichaDomain entrada = AgregacionEstadoEvaluacionFichaDomain.crear(
            EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.DESCARTADA.getId()),
            representante);
    private final EstadoEvaluacionFichaDomain enEvaluacion =
            EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.EN_EVALUACION.getId());
    private final EstadoActualFicha fichaDisponible =
            new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

    @Test
    void debePasar_cuandoTodoCumple() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, true, true, enEvaluacion, fichaDisponible))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoEvaluacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, true, enEvaluacion, fichaDisponible))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPropia_cuandoEsAjena() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, false, enEvaluacion, fichaDisponible))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarNoDescartable_cuandoNoEstaEnEvaluacion() {
        // Arrange
        var descartada = EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.DESCARTADA.getId());
        var finalizada = EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.APROBADA.getId());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, descartada, fichaDisponible))
                .isInstanceOf(EvaluacionNoDescartableException.class);
        assertThatThrownBy(() -> validator.validar(entrada, true, true, finalizada, fichaDisponible))
                .isInstanceOf(EvaluacionNoDescartableException.class);
    }

    @Test
    void debeLanzarFichaNoDisponible_cuandoFichaNoEstaDisponible() {
        // Arrange
        var fichaAprobada = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, true, true, enEvaluacion, fichaAprobada))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }

    @Test
    void debeAplicarExistenciaAntesQuePropiedadYDescartable_cuandoTodasLasReglasFallan() {
        // Arrange
        var descartada = EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.DESCARTADA.getId());
        var fichaAprobada = new EstadoActualFicha(UtilUUID.generarNuevoUUID(), EstadoFicha.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, false, false, descartada, fichaAprobada))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
        assertThatThrownBy(() -> validator.validar(entrada, true, false, descartada, fichaAprobada))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
        assertThatThrownBy(() -> validator.validar(entrada, true, true, descartada, fichaAprobada))
                .isInstanceOf(EvaluacionNoDescartableException.class);
    }
}
