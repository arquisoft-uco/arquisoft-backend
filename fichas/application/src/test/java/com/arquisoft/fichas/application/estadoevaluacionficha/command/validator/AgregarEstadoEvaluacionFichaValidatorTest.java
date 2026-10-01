package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AgregarEstadoEvaluacionFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEnEvaluacionNoManualException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionTerminalException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstadoEvaluacionFichaValidatorTest {

    private final AgregarEstadoEvaluacionFichaValidatorImpl validator =
            new AgregarEstadoEvaluacionFichaValidatorImpl();

    private final UUID evaluacion = UUID.randomUUID();
    private final UUID representante = UUID.randomUUID();

    private AgregacionEstadoEvaluacionFichaDomain entrada(EstadoEvaluacion estado) {
        return AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, estado.getId()), representante);
    }

    @Test
    void debePasar_cuandoLaEvaluacionExisteEsPropiaYNoHayEstadoPrevio() {
        // Act / Assert
        assertThatCode(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionNoEncontrada_cuandoLaEvaluacionNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), false, true, false,
                EstadoEvaluacionFichaDomain.VACIO))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class)
                .hasMessageContaining(evaluacion.toString());
    }

    @Test
    void debeLanzarEvaluacionNoPropia_cuandoElRepresentanteNoEsElDueno() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, false, false,
                EstadoEvaluacionFichaDomain.VACIO))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarEstadoDuplicado_cuandoEseEstadoYaFueRegistrado() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), true, true, true,
                EstadoEvaluacionFichaDomain.VACIO))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);
    }

    @Test
    void debeLanzarEstadoNoManual_cuandoSeIntentaRegistrarEN_EVALUACION() {
        // Act / Assert — EN_EVALUACION lo asigna el sistema, no un representante
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.EN_EVALUACION), true, true, false,
                EstadoEvaluacionFichaDomain.VACIO))
                .isInstanceOf(EstadoEnEvaluacionNoManualException.class);
    }

    @Test
    void debeLanzarEstadoTerminal_cuandoLaEvaluacionYaEstaCerrada() {
        // Arrange
        var ultimoEstado = EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacion, EstadoEvaluacion.APROBADA.getId());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.NO_APROBADA), true, true, false,
                ultimoEstado))
                .isInstanceOf(EstadoEvaluacionTerminalException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaEvaluacion_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato
        assertThatThrownBy(() -> validator.validar(entrada(EstadoEvaluacion.APROBADA), false, false, true,
                EstadoEvaluacionFichaDomain.VACIO))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }
}
