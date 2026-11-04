package com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.finder.EvaluacionExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.ConsultarEvaluacionesJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesJuradoUseCaseImplTest {

    @Mock
    private EvaluacionExisteQueryFinder evaluacionExisteQueryFinder;

    @Mock
    private ConsultarEvaluacionesJuradoValidator consultarEvaluacionesJuradoValidator;

    @Mock
    private EvaluacionJuradoQueryOutputPort evaluacionJuradoQueryOutputPort;

    @Mock
    private AppLogger logger;

    private ConsultarEvaluacionesJuradoUseCaseImpl useCase;

    @Test
    void debeConsultarEnOrden_yRetornarElResultadoDelPuerto_cuandoLaEvaluacionExiste() {
        // Arrange
        useCase = new ConsultarEvaluacionesJuradoUseCaseImpl(
                evaluacionExisteQueryFinder, consultarEvaluacionesJuradoValidator,
                evaluacionJuradoQueryOutputPort, logger);
        var evaluacion = UUID.randomUUID();
        var criteria = EvaluacionJuradoCriteria.builder().evaluacion(evaluacion).pagina(0).tamanio(10).build();
        var readModel = new EvaluacionJuradoReadModel(
                UUID.randomUUID(), new EvaluacionJuradoReadModel.Jurado(UUID.randomUUID(), "Ana", "ana@uco.edu.co"));
        var esperado = PaginatedResult.of(List.of(readModel), 0, 10, 1);

        when(evaluacionExisteQueryFinder.obtener(evaluacion)).thenReturn(true);
        when(evaluacionJuradoQueryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);

        verify(evaluacionExisteQueryFinder, times(1)).obtener(evaluacion);
        verify(evaluacionJuradoQueryOutputPort, times(1)).consultarTodas(criteria);

        InOrder orden = inOrder(evaluacionExisteQueryFinder, consultarEvaluacionesJuradoValidator,
                evaluacionJuradoQueryOutputPort);
        orden.verify(evaluacionExisteQueryFinder).obtener(evaluacion);
        orden.verify(consultarEvaluacionesJuradoValidator).validar(evaluacion, true);
        orden.verify(evaluacionJuradoQueryOutputPort).consultarTodas(criteria);

        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacion), eq(criteria.getPagina()),
                eq(criteria.getTamanio()), eq(criteria.tieneFiltros()), eq(criteria.tieneOrden()));
        verify(logger).debug(eq(EvaluacionJuradoKey.LOG_CONSULTA_COMPLETADA), eq(esperado.getTotalElements()),
                eq(criteria.getPagina()), eq(criteria.getTamanio()));
    }

    @Test
    void debePropagarLaExcepcionDelValidator_sinConsultarElPuerto_cuandoLaEvaluacionNoExiste() {
        // Arrange
        useCase = new ConsultarEvaluacionesJuradoUseCaseImpl(
                evaluacionExisteQueryFinder, consultarEvaluacionesJuradoValidator,
                evaluacionJuradoQueryOutputPort, logger);
        var evaluacion = UUID.randomUUID();
        var criteria = EvaluacionJuradoCriteria.builder().evaluacion(evaluacion).build();

        when(evaluacionExisteQueryFinder.obtener(evaluacion)).thenReturn(false);
        var excepcion = new EvaluacionNoEncontradaException(evaluacion);
        doThrow(excepcion).when(consultarEvaluacionesJuradoValidator).validar(evaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria)).isSameAs(excepcion);

        verifyNoInteractions(evaluacionJuradoQueryOutputPort);
    }
}
