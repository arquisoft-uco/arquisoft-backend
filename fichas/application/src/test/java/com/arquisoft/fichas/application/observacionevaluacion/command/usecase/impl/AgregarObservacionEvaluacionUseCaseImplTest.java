package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.RepresentantePropietarioObservacionEvaluacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.AgregarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarObservacionEvaluacionUseCaseImplTest {

    @Mock
    private EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;

    @Mock
    private RepresentantePropietarioObservacionEvaluacionFinder representantePropietarioObservacionEvaluacionFinder;

    @Mock
    private UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;

    @Mock
    private ObservacionEvaluacionDuplicadaFinder observacionEvaluacionDuplicadaFinder;

    @Mock
    private AgregarObservacionEvaluacionValidator agregarObservacionEvaluacionValidator;

    @Mock
    private ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarObservacionEvaluacionUseCaseImpl agregarObservacionEvaluacionUseCase;

    private final UUID evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
    private final AgregacionObservacionEvaluacionDomain entrada = AgregacionObservacionEvaluacionDomain.crear(
            ObservacionEvaluacionDomain.crear(evaluacionFichaPerfil, "Observación válida"),
            UtilUUID.generarNuevoUUID());

    @Test
    void debeRegistrarYRetornarId_cuandoValidacionPasa() {
        // Arrange
        stubFinders(true, true, estadoActual(EstadoEvaluacion.EN_EVALUACION), false);

        // Act
        var resultado = agregarObservacionEvaluacionUseCase.ejecutar(entrada);

        // Assert — resultado y entidad persistida
        var observacionEvaluacion = entrada.getObservacionEvaluacion();
        assertThat(resultado).isEqualTo(observacionEvaluacion.getId());
        var captor = ArgumentCaptor.forClass(ObservacionEvaluacionEntity.class);
        verify(observacionEvaluacionOutputPort).registrarObservacion(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new ObservacionEvaluacionEntity(
                observacionEvaluacion.getId(), evaluacionFichaPerfil, "Observación válida"));
        verify(logger).info(any(ClaveMensaje.class), eq(observacionEvaluacion.getId()), eq(evaluacionFichaPerfil));

        // Assert — presupuesto de I/O: un viaje por finder
        verify(evaluacionFichaExisteFinder, times(1)).obtener(evaluacionFichaPerfil);
        verify(representantePropietarioObservacionEvaluacionFinder, times(1)).obtener(entrada);
        verify(ultimoEstadoEvaluacionFichaFinder, times(1)).obtener(evaluacionFichaPerfil);
        verify(observacionEvaluacionDuplicadaFinder, times(1)).obtener(entrada);

        // Assert — orden: finders -> validator -> persistencia
        var orden = inOrder(evaluacionFichaExisteFinder, representantePropietarioObservacionEvaluacionFinder,
                ultimoEstadoEvaluacionFichaFinder, observacionEvaluacionDuplicadaFinder,
                agregarObservacionEvaluacionValidator, observacionEvaluacionOutputPort);
        orden.verify(evaluacionFichaExisteFinder).obtener(evaluacionFichaPerfil);
        orden.verify(representantePropietarioObservacionEvaluacionFinder).obtener(entrada);
        orden.verify(ultimoEstadoEvaluacionFichaFinder).obtener(evaluacionFichaPerfil);
        orden.verify(observacionEvaluacionDuplicadaFinder).obtener(entrada);
        orden.verify(agregarObservacionEvaluacionValidator)
                .validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, false);
        orden.verify(observacionEvaluacionOutputPort).registrarObservacion(any());
    }

    @Test
    void noDebePersistir_cuandoValidatorLanza() {
        // Arrange
        stubFinders(true, true, estadoActual(EstadoEvaluacion.EN_EVALUACION), true);
        doThrow(new ObservacionEvaluacionDuplicadaException(evaluacionFichaPerfil, entrada.getObservacion()))
                .when(agregarObservacionEvaluacionValidator)
                .validar(entrada, true, true, EstadoEvaluacion.EN_EVALUACION, true);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class);
        verify(observacionEvaluacionOutputPort, never()).registrarObservacion(any());
        verify(logger, never()).info(any(ClaveMensaje.class),
                eq(entrada.getObservacionEvaluacion().getId()), eq(evaluacionFichaPerfil));
    }

    @Test
    void debeEntregarEstadoVacioAlValidator_cuandoLaEvaluacionNoExiste() {
        // Arrange — el finder devuelve el centinela y el use case no revienta al leer su estado
        stubFinders(false, false, EstadoEvaluacionFichaDomain.VACIO, false);
        doThrow(new EvaluacionFichaPerfilNoEncontradaException(evaluacionFichaPerfil))
                .when(agregarObservacionEvaluacionValidator)
                .validar(entrada, false, false, EstadoEvaluacion.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> agregarObservacionEvaluacionUseCase.ejecutar(entrada))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
        verify(observacionEvaluacionOutputPort, never()).registrarObservacion(any());
    }

    private void stubFinders(boolean evaluacionExiste, boolean esPropietario,
                             EstadoEvaluacionFichaDomain ultimoEstado, boolean observacionYaExiste) {
        when(evaluacionFichaExisteFinder.obtener(evaluacionFichaPerfil)).thenReturn(evaluacionExiste);
        when(representantePropietarioObservacionEvaluacionFinder.obtener(entrada)).thenReturn(esPropietario);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacionFichaPerfil)).thenReturn(ultimoEstado);
        when(observacionEvaluacionDuplicadaFinder.obtener(entrada)).thenReturn(observacionYaExiste);
    }

    private EstadoEvaluacionFichaDomain estadoActual(EstadoEvaluacion estadoEvaluacion) {
        return EstadoEvaluacionFichaDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil, estadoEvaluacion, Instant.now());
    }
}
