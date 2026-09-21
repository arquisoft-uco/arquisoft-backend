package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionItemJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.OtraObservacionItemJuradoConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.ModificarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarObservacionItemJuradoUseCaseImplTest {

    @Mock
    private ObservacionItemJuradoOutputPort outputPort;

    @Mock
    private ObservacionItemJuradoPorIdFinder observacionItemJuradoPorIdFinder;

    @Mock
    private OtraObservacionItemJuradoConDescripcionExisteFinder otraObservacionConDescripcionExisteFinder;

    @Mock
    private EvaluacionJuradoFinalizadaPorObservacionFinder evaluacionJuradoFinalizadaPorObservacionFinder;

    @Mock
    private ModificarObservacionItemJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarObservacionItemJuradoUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcionEnOrden_cuandoTodoEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(id, "  Nueva descripción de la observación  ");
        var observacion = ObservacionItemJuradoDomain.reconstruir(
                id, evaluacionCuantitativaJurado, "Descripción anterior");
        when(observacionItemJuradoPorIdFinder.obtener(id)).thenReturn(observacion);
        when(evaluacionJuradoFinalizadaPorObservacionFinder.obtener(id)).thenReturn(false);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(false);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(logger, observacionItemJuradoPorIdFinder, evaluacionJuradoFinalizadaPorObservacionFinder,
                otraObservacionConDescripcionExisteFinder, validator, outputPort);
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_MODIFICANDO, id);
        orden.verify(observacionItemJuradoPorIdFinder).obtener(id);
        orden.verify(evaluacionJuradoFinalizadaPorObservacionFinder).obtener(id);
        orden.verify(otraObservacionConDescripcionExisteFinder).obtener(modificacion);
        orden.verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_MODIFICAR, true, false, false);
        orden.verify(validator).validar(modificacion, observacion, false, false);
        orden.verify(outputPort).actualizarDescripcion(id, "Nueva descripción de la observación");
        orden.verify(logger).info(ObservacionItemJuradoKey.LOG_MODIFICADA, id);
        verify(observacionItemJuradoPorIdFinder, times(1)).obtener(any());
        verify(evaluacionJuradoFinalizadaPorObservacionFinder, times(1)).obtener(any());
        verify(otraObservacionConDescripcionExisteFinder, times(1)).obtener(any());
    }

    @Test
    void debeDetenerFlujo_cuandoLaObservacionNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(id, "Nueva descripción de la observación");
        when(observacionItemJuradoPorIdFinder.obtener(id)).thenReturn(ObservacionItemJuradoDomain.VACIO);
        when(evaluacionJuradoFinalizadaPorObservacionFinder.obtener(id)).thenReturn(false);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(false);
        doThrow(new ObservacionItemJuradoNoEncontradaException(id))
                .when(validator).validar(modificacion, ObservacionItemJuradoDomain.VACIO, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ObservacionItemJuradoNoEncontradaException.class);
        verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_MODIFICAR, false, false, false);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ObservacionItemJuradoKey.LOG_MODIFICADA, id);
    }

    @Test
    void debeDetenerFlujo_cuandoLaEvaluacionJuradoEstaFinalizada() {
        // Arrange
        var id = UUID.randomUUID();
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(id, "Nueva descripción de la observación");
        var observacion = ObservacionItemJuradoDomain.reconstruir(
                id, evaluacionCuantitativaJurado, "Descripción anterior");
        when(observacionItemJuradoPorIdFinder.obtener(id)).thenReturn(observacion);
        when(evaluacionJuradoFinalizadaPorObservacionFinder.obtener(id)).thenReturn(true);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(false);
        doThrow(new EvaluacionJuradoFinalizadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(modificacion, observacion, true, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
        verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_MODIFICAR, true, true, false);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ObservacionItemJuradoKey.LOG_MODIFICADA, id);
    }

    @Test
    void debeDetenerFlujo_cuandoOtraObservacionYaTieneLaDescripcion() {
        // Arrange
        var id = UUID.randomUUID();
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(id, "Descripción repetida");
        var observacion = ObservacionItemJuradoDomain.reconstruir(
                id, evaluacionCuantitativaJurado, "Descripción anterior");
        when(observacionItemJuradoPorIdFinder.obtener(id)).thenReturn(observacion);
        when(evaluacionJuradoFinalizadaPorObservacionFinder.obtener(id)).thenReturn(false);
        when(otraObservacionConDescripcionExisteFinder.obtener(modificacion)).thenReturn(true);
        doThrow(new DescripcionObservacionItemJuradoDuplicadaException(evaluacionCuantitativaJurado))
                .when(validator).validar(modificacion, observacion, false, true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(DescripcionObservacionItemJuradoDuplicadaException.class);
        verify(logger).debug(ObservacionItemJuradoKey.LOG_VERIFICACION_MODIFICAR, true, false, true);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ObservacionItemJuradoKey.LOG_MODIFICADA, id);
    }
}
