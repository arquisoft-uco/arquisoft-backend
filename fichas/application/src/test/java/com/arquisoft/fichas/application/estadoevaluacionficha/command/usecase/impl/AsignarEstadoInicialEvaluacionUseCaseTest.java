package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AsignarEstadoInicialEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialEvaluacionUseCaseTest {

    @Mock
    private EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Mock
    private EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;

    @Mock
    private AsignarEstadoInicialEvaluacionValidator asignarEstadoInicialEvaluacionValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstadoInicialEvaluacionUseCaseImpl asignarEstadoInicialEvaluacionUseCase;

    private final EvaluacionFichaPerfilDomain evaluacion =
            EvaluacionFichaPerfilDomain.crear(UUID.randomUUID(), UUID.randomUUID());

    @Test
    void debeRegistrarElEstadoEnEvaluacion_cuandoLaEvaluacionExiste() {
        // Arrange
        when(evaluacionFichaExisteFinder.obtener(evaluacion.getId())).thenReturn(true);

        // Act
        asignarEstadoInicialEvaluacionUseCase.ejecutar(evaluacion);

        // Assert
        ArgumentCaptor<EstadoEvaluacionFichaEntity> captor =
                ArgumentCaptor.forClass(EstadoEvaluacionFichaEntity.class);
        verify(estadoEvaluacionFichaOutputPort).registrarEstadoInicial(captor.capture());

        EstadoEvaluacionFichaEntity estado = captor.getValue();
        assertThat(estado.evaluacionFichaPerfil()).isEqualTo(evaluacion.getId());
        assertThat(estado.estadoEvaluacion()).isEqualTo(EstadoEvaluacion.EN_EVALUACION.getId());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        when(evaluacionFichaExisteFinder.obtener(evaluacion.getId())).thenReturn(true);

        // Act
        asignarEstadoInicialEvaluacionUseCase.ejecutar(evaluacion);

        // Assert
        InOrder inOrder = inOrder(evaluacionFichaExisteFinder,
                asignarEstadoInicialEvaluacionValidator, estadoEvaluacionFichaOutputPort);
        inOrder.verify(evaluacionFichaExisteFinder).obtener(evaluacion.getId());
        inOrder.verify(asignarEstadoInicialEvaluacionValidator).validar(evaluacion.getId(), true);
        inOrder.verify(estadoEvaluacionFichaOutputPort).registrarEstadoInicial(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaEvaluacionNoExiste() {
        // Arrange
        when(evaluacionFichaExisteFinder.obtener(evaluacion.getId())).thenReturn(false);
        doThrow(new EvaluacionFichaPerfilNoEncontradaException(evaluacion.getId()))
                .when(asignarEstadoInicialEvaluacionValidator).validar(evaluacion.getId(), false);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialEvaluacionUseCase.ejecutar(evaluacion))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);

        verify(estadoEvaluacionFichaOutputPort, never()).registrarEstadoInicial(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        when(evaluacionFichaExisteFinder.obtener(evaluacion.getId())).thenReturn(true);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estadoEvaluacionFichaOutputPort).registrarEstadoInicial(any());

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialEvaluacionUseCase.ejecutar(evaluacion))
                .isInstanceOf(InfrastructureException.class);
    }
}
