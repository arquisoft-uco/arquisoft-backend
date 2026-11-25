package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoDescartadaNoAsignableException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEnEvaluacionNoManualException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionTerminalException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionAprobadaConObservacionesException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionSinObservacionesException;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoEvaluacionFichaValidatorTest {

    private final AgregarEstadoEvaluacionFichaValidatorImpl validator =
            new AgregarEstadoEvaluacionFichaValidatorImpl();

    private final UUID evaluacion = UUID.randomUUID();
    private final UUID representante = UUID.randomUUID();

    private static final EstadoActualFicha FICHA_DISPONIBLE = new EstadoActualFicha(
            UUID.randomUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

    private AgregacionEstadoEvaluacionFichaDomain entrada(EstadoEvaluacion estado) {
        return AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, estado.getId()), representante);
    }

    @Test
    void debePasar_cuandoLaEvaluacionExisteEsPropiaYNoHayEstadoPrevio() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionNoEncontrada_cuandoLaEvaluacionNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), false, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class)
                .hasMessageContaining(evaluacion.toString());
    }

    @Test
    void debeLanzarEvaluacionNoPropia_cuandoElRepresentanteNoEsElDueno() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, false, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarEstadoDuplicado_cuandoEseEstadoYaFueRegistrado() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, true,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);
    }

    @Test
    void debeLanzarEstadoNoManual_cuandoSeIntentaRegistrarEN_EVALUACION() {
        // Act / Assert — EN_EVALUACION lo asigna el sistema, no un representante
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.EN_EVALUACION), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EstadoEnEvaluacionNoManualException.class);
    }

    @Test
    void debeLanzarEstadoTerminal_cuandoLaEvaluacionYaEstaCerrada() {
        // Arrange
        var ultimoEstado = EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacion, EstadoEvaluacion.APROBADA.getId());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.NO_APROBADA), true, true, false,
                ultimoEstado, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EstadoEvaluacionTerminalException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaEvaluacion_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), false, false, true,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarObservacionesNoPermitidas_cuandoSePideAprobadaYLaEvaluacionTieneObservaciones() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 2, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionAprobadaConObservacionesException.class);
    }

    @Test
    void debeLanzarObservacionesRequeridas_cuandoSePideAprobadaConObservacionesSinNinguna() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES), true, true,
                false, EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EvaluacionSinObservacionesException.class);
    }

    @Test
    void debePasar_cuandoSePideAprobadaConObservacionesYHayAlMenosUna() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA_CON_OBSERVACIONES), true, true,
                false, EstadoEvaluacionFichaDomain.VACIO, 1, FICHA_DISPONIBLE))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoSePideNoAprobadaConObservaciones() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada(EstadoEvaluacion.NO_APROBADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 3, FICHA_DISPONIBLE))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDescartadaNoAsignable_cuandoSePideDescartada() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.DESCARTADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, FICHA_DISPONIBLE))
                .isInstanceOf(EstadoDescartadaNoAsignableException.class);
    }

    @Test
    void debeReportarPrimeroElEstadoTerminal_cuandoTambienFallaLaCoherenciaConLasObservaciones() {
        // Arrange
        var ultimoEstado = EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacion, EstadoEvaluacion.NO_APROBADA.getId());

        // Act / Assert — la coherencia con las observaciones es la última regla
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, false,
                ultimoEstado, 2, FICHA_DISPONIBLE))
                .isInstanceOf(EstadoEvaluacionTerminalException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"EN_CONSTRUCCION", "DESCARTADA", "APROBADA",
            "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarFichaNoDisponible_cuandoLaFichaNoEstaDisponible(EstadoFicha estado) {
        // Arrange
        var estadoFicha = new EstadoActualFicha(UUID.randomUUID(), estado);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO, 0, estadoFicha))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }

    @Test
    void debeAplicarLaReglaDeEvaluacionTerminalAntesQueLaDisponibilidad_cuandoAmbasFallan() {
        // Arrange
        var ultimoEstado = EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacion, EstadoEvaluacion.APROBADA.getId());
        var estadoFicha = new EstadoActualFicha(UUID.randomUUID(), EstadoFicha.APROBADA);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.NO_APROBADA), true, true, false,
                ultimoEstado, 0, estadoFicha))
                .isInstanceOf(EstadoEvaluacionTerminalException.class);
    }
}
