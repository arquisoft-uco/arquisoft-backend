package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.CambiarPuntajeEvaluacionCuantitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoPorIdFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        var estado = EstadoEvaluacionJuradoDomain.reconstruir(evaluacionJurado, jurado, EstadoEvaluacion.PENDIENTE);
        var itemDomain = ItemCuantitativoJuradoDomain.reconstruir(
                item, "Rigor", "Descripción", UUID.randomUUID(), 500);
        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado)).thenReturn(evaluacion);
        when(evaluacionJuradoEstadoFinder.obtener(evaluacionJurado)).thenReturn(estado);
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(itemDomain);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        InOrder orden = inOrder(logger, evaluacionCuantitativaJuradoPorIdFinder,
                evaluacionJuradoEstadoFinder, itemCuantitativoJuradoPorIdFinder, validator, outputPort);
        orden.verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_CAMBIANDO_PUNTAJE,
                evaluacionCuantitativaJurado, 300);
        orden.verify(evaluacionCuantitativaJuradoPorIdFinder).obtener(evaluacionCuantitativaJurado);
        orden.verify(evaluacionJuradoEstadoFinder).obtener(evaluacionJurado);
        orden.verify(itemCuantitativoJuradoPorIdFinder).obtener(item);
        orden.verify(logger).debug(EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                true, jurado, EstadoEvaluacion.PENDIENTE.getId());
        orden.verify(validator).validar(cambio, evaluacion, estado, itemDomain);
        orden.verify(outputPort).actualizar(
                new EvaluacionCuantitativaJuradoEntity(evaluacionCuantitativaJurado, evaluacionJurado, item, 300));
        orden.verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO,
                evaluacionCuantitativaJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoEvaluacionNoExiste() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, UUID.randomUUID(), 300);
        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado))
                .thenReturn(EvaluacionCuantitativaJuradoDomain.VACIO);
        when(evaluacionJuradoEstadoFinder.obtener(UtilUUID.obtenerUUIDPorDefecto()))
                .thenReturn(EstadoEvaluacionJuradoDomain.VACIO);
        when(itemCuantitativoJuradoPorIdFinder.obtener(UtilUUID.obtenerUUIDPorDefecto()))
                .thenReturn(ItemCuantitativoJuradoDomain.VACIO);
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(evaluacionCuantitativaJurado))
                .when(validator).validar(
                        cambio,
                        EvaluacionCuantitativaJuradoDomain.VACIO,
                        EstadoEvaluacionJuradoDomain.VACIO,
                        ItemCuantitativoJuradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
        verify(logger).debug(EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                false, UtilUUID.obtenerUUIDPorDefecto(), EstadoEvaluacion.VACIO.getId());
        verify(outputPort, never()).actualizar(any());
        verify(logger, never()).info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO,
                evaluacionCuantitativaJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoElValidadorRechaza() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                evaluacionCuantitativaJurado, jurado, 300);
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, evaluacionJurado, item, 250);
        var estado = EstadoEvaluacionJuradoDomain.reconstruir(evaluacionJurado, jurado, EstadoEvaluacion.FINALIZADA);
        var itemDomain = ItemCuantitativoJuradoDomain.reconstruir(
                item, "Rigor", "Descripción", UUID.randomUUID(), 500);
        when(evaluacionCuantitativaJuradoPorIdFinder.obtener(evaluacionCuantitativaJurado)).thenReturn(evaluacion);
        when(evaluacionJuradoEstadoFinder.obtener(evaluacionJurado)).thenReturn(estado);
        when(itemCuantitativoJuradoPorIdFinder.obtener(item)).thenReturn(itemDomain);
        doThrow(new EvaluacionJuradoFinalizadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(cambio, evaluacion, estado, itemDomain);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
        verify(outputPort, never()).actualizar(any());
        verify(logger, never()).info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO,
                evaluacionCuantitativaJurado);
    }
}
