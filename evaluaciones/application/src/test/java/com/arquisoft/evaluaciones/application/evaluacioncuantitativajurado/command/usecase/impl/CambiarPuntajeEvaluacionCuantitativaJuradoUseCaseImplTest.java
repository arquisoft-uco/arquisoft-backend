package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.CambiarPuntajeEvaluacionCuantitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.SolicitudEstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoPorIdFinder;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarPuntajeEvaluacionCuantitativaJuradoUseCaseImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @Mock
    private EvaluacionCuantitativaJuradoPorIdFinder evaluacionCuantitativaJuradoPorIdFinder;

    @Mock
    private EvaluacionJuradoEstadoFinder evaluacionJuradoEstadoFinder;

    @Mock
    private ItemCuantitativoJuradoPorIdFinder itemCuantitativoJuradoPorIdFinder;

    @Mock
    private CambiarPuntajeEvaluacionCuantitativaJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarPuntajeEvaluacionCuantitativaJuradoUseCaseImpl useCase;

    @Test
    void debeCambiarPuntajeEnOrden_cuandoTodoEsValido() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);
        var estado = new EstadoEvaluacionJuradoEntity(true, false);
        var itemDomain = ItemCuantitativoJuradoDomain.reconstruir(
                item, "Rigor", "Descripción", UUID.randomUUID(), 500);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoEstadoFinder.obtener(new SolicitudEstadoEvaluacionJurado(evaluacionJurado, jurado)))
                .thenReturn(estado);
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(Optional.of(itemDomain));

        // Act
        useCase.ejecutar(cambio);

        // Assert
        InOrder orden = inOrder(logger, evaluacionCuantitativaJuradoPorIdFinder,
                evaluacionJuradoEstadoFinder, itemCuantitativoJuradoPorIdFinder, validator, outputPort);
        orden.verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_CAMBIANDO_PUNTAJE,
                evaluacionCuantitativaJurado, 300);
        orden.verify(evaluacionCuantitativaJuradoPorIdFinder).obtener(evaluacionCuantitativaJurado);
        orden.verify(evaluacionJuradoEstadoFinder).obtener(
                new SolicitudEstadoEvaluacionJurado(evaluacionJurado, jurado));
        orden.verify(itemCuantitativoJuradoPorIdFinder).obtener(item);
        orden.verify(logger).debug(EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                true, true, false);
        orden.verify(validator).validar(cambio, evaluacion, estado, 500);
        orden.verify(outputPort).cambiarPuntaje(evaluacionCuantitativaJurado, 300);
        orden.verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO,
                evaluacionCuantitativaJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoEvaluacionNoExiste() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.empty());
        when(evaluacionJuradoEstadoFinder.obtener(any()))
                .thenReturn(new EstadoEvaluacionJuradoEntity(false, false));
        when(itemCuantitativoJuradoPorIdFinder.obtener(any())).thenReturn(Optional.empty());
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(cambio), any(), any(), anyInt());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
        verify(outputPort, never()).cambiarPuntaje(any(), any());
        verify(logger, never()).info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO,
                evaluacionCuantitativaJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoNoPerteneceAlJurado() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoEstadoFinder.obtener(any()))
                .thenReturn(new EstadoEvaluacionJuradoEntity(false, false));
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(Optional.empty());
        doThrow(new EvaluacionCuantitativaJuradoNoPerteneceJuradoException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(cambio), any(), any(), anyInt());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoPerteneceJuradoException.class);
        verify(outputPort, never()).cambiarPuntaje(any(), any());
    }

    @Test
    void debeDetenerFlujo_cuandoEvaluacionJuradoFinalizada() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoEstadoFinder.obtener(any()))
                .thenReturn(new EstadoEvaluacionJuradoEntity(true, true));
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(Optional.empty());
        doThrow(new EvaluacionJuradoFinalizadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(eq(cambio), any(), any(), anyInt());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
        verify(outputPort, never()).cambiarPuntaje(any(), any());
    }

    @Test
    void debeDetenerFlujo_cuandoPuntajeExcedeValorItem() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);
        var itemDomain = ItemCuantitativoJuradoDomain.reconstruir(
                item, "Rigor", "Descripción", UUID.randomUUID(), 200);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoEstadoFinder.obtener(any()))
                .thenReturn(new EstadoEvaluacionJuradoEntity(true, false));
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(Optional.of(itemDomain));
        doThrow(new PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException(300, 200))
                .when(validator).validar(cambio, evaluacion, new EstadoEvaluacionJuradoEntity(true, false), 200);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException.class);
        verify(outputPort, never()).cambiarPuntaje(any(), any());
    }

    @Test
    void debeUsarValorMaximoPorDefecto_cuandoItemNoExiste() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);
        var estado = new EstadoEvaluacionJuradoEntity(true, false);

        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(Optional.of(evaluacion));
        when(evaluacionJuradoEstadoFinder.obtener(new SolicitudEstadoEvaluacionJurado(evaluacionJurado, jurado)))
                .thenReturn(estado);
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(Optional.empty());

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(validator).validar(cambio, evaluacion, estado, Integer.MAX_VALUE);
        verify(outputPort).cambiarPuntaje(evaluacionCuantitativaJurado, 300);
    }
}
