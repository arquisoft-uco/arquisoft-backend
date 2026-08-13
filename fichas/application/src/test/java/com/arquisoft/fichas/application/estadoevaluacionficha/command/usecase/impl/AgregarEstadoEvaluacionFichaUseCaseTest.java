package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EstadoEnEvaluacionExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.RepresentantePropietarioEvaluacionFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstadoEvaluacionFichaUseCaseTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;

    @Mock
    private RepresentantePropietarioEvaluacionFinder representantePropietarioEvaluacionFinder;

    @Mock
    private EstadoEnEvaluacionExisteFinder estadoEnEvaluacionExisteFinder;

    @Mock
    private UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;

    @Mock
    private AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;

    @Mock
    private AppLogger logger;

    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private AgregarEstadoEvaluacionFichaUseCaseImpl agregarEstadoEvaluacionFichaUseCase;

    private final UUID evaluacion = UUID.randomUUID();
    private final UUID representante = UUID.randomUUID();

    @Test
    void debeAgregarElEstado_cuandoDatosValidos() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, false);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion))
                .thenReturn(Optional.of(EstadoEvaluacionFichaDomain.crear(evaluacion)));

        // Act
        UUID resultado = agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isNotNull();
        verify(estadoEvaluacionFichaOutputPort, times(1)).agregarEstado(any());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        var ultimoEstado = EstadoEvaluacionFichaDomain.crear(evaluacion);
        stubConsultas(entrada, true, true, false);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion)).thenReturn(Optional.of(ultimoEstado));

        // Act
        agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteFinder, representantePropietarioEvaluacionFinder,
                estadoEnEvaluacionExisteFinder, ultimoEstadoEvaluacionFichaFinder,
                agregarEstadoEvaluacionFichaValidator, estadoEvaluacionFichaOutputPort);
        inOrder.verify(evaluacionFichaExisteFinder).obtener(evaluacion);
        inOrder.verify(representantePropietarioEvaluacionFinder).obtener(entrada);
        inOrder.verify(estadoEnEvaluacionExisteFinder).obtener(entrada);
        inOrder.verify(ultimoEstadoEvaluacionFichaFinder).obtener(evaluacion);
        inOrder.verify(agregarEstadoEvaluacionFichaValidator)
                .validar(entrada, true, true, false, ultimoEstado);
        inOrder.verify(estadoEvaluacionFichaOutputPort).agregarEstado(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, false, false, false);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion)).thenReturn(Optional.empty());
        doThrow(new EvaluacionFichaPerfilNoEncontradaException(evaluacion))
                .when(agregarEstadoEvaluacionFichaValidator).validar(entrada, false, false, false, EstadoEvaluacionFichaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).agregarEstado(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElRepresentanteNoEsPropietario() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, false, false);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion)).thenReturn(Optional.empty());
        doThrow(new EvaluacionFichaNoPropiaException(evaluacion))
                .when(agregarEstadoEvaluacionFichaValidator).validar(entrada, true, false, false, EstadoEvaluacionFichaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada))
                .isInstanceOf(EvaluacionFichaNoPropiaException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).agregarEstado(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElEstadoYaEstaRegistrado() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, true);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion)).thenReturn(Optional.empty());
        doThrow(new EstadoEvaluacionDuplicadoException(evaluacion, EstadoEvaluacion.APROBADA.getId()))
                .when(agregarEstadoEvaluacionFichaValidator).validar(entrada, true, true, true, EstadoEvaluacionFichaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).agregarEstado(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, false);
        when(ultimoEstadoEvaluacionFichaFinder.obtener(evaluacion))
                .thenReturn(Optional.of(EstadoEvaluacionFichaDomain.crear(evaluacion)));
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estadoEvaluacionFichaOutputPort).agregarEstado(any());

        // Act & Assert
        assertThatThrownBy(() -> agregarEstadoEvaluacionFichaUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultas(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste,
                               boolean esPropietario, boolean estadoYaExiste) {
        when(evaluacionFichaExisteFinder.obtener(evaluacion)).thenReturn(evaluacionExiste);
        when(representantePropietarioEvaluacionFinder.obtener(entrada)).thenReturn(esPropietario);
        when(estadoEnEvaluacionExisteFinder.obtener(entrada)).thenReturn(estadoYaExiste);
    }

    private AgregacionEstadoEvaluacionFichaDomain entrada() {
        return AgregacionEstadoEvaluacionFichaDomain.crear(
                EstadoEvaluacionFichaDomain.crearConEstado(evaluacion, EstadoEvaluacion.APROBADA.getId()),
                representante);
    }

    // El puerto devuelve la entidad; convertirla al enum de dominio es tarea del mapper.
}
