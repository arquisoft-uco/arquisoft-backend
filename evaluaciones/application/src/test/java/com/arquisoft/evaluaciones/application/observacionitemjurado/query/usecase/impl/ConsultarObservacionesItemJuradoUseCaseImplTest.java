package com.arquisoft.evaluaciones.application.observacionitemjurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.finder.EvaluacionCuantitativaJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.secondaryport.ObservacionItemJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator.ConsultarObservacionesItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
class ConsultarObservacionesItemJuradoUseCaseImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoExisteQueryFinder evaluacionCuantitativaJuradoExisteQueryFinder;

    @Mock
    private ConsultarObservacionesItemJuradoValidator validator;

    @Mock
    private ObservacionItemJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesItemJuradoUseCaseImpl useCase;

    private static ObservacionItemJuradoCriteria criteriaValido() {
        return ObservacionItemJuradoCriteria.builder()
                .evaluacionCuantitativaJurado(UUID.randomUUID())
                .build();
    }

    @Test
    void debeRetornarLaPagina_cuandoLaEvaluacionExiste() {
        // Arrange
        var criteria = criteriaValido();
        var evaluacion = criteria.getEvaluacionCuantitativaJurado();
        var esperado = PaginatedResult.of(
                List.of(new ObservacionItemJuradoReadModel(UUID.randomUUID(), "Sustenta el puntaje")), 0, 10, 1);
        when(evaluacionCuantitativaJuradoExisteQueryFinder.obtener(evaluacion)).thenReturn(true);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(evaluacionCuantitativaJuradoExisteQueryFinder, validator, queryOutputPort);
        orden.verify(evaluacionCuantitativaJuradoExisteQueryFinder).obtener(evaluacion);
        orden.verify(validator).validar(evaluacion, true);
        orden.verify(queryOutputPort).consultarTodas(criteria);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoLaEvaluacionNoTieneObservaciones() {
        // Arrange
        var criteria = criteriaValido();
        when(evaluacionCuantitativaJuradoExisteQueryFinder.obtener(criteria.getEvaluacionCuantitativaJurado()))
                .thenReturn(true);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeLanzarYNoConsultar_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var criteria = criteriaValido();
        var evaluacion = criteria.getEvaluacionCuantitativaJurado();
        when(evaluacionCuantitativaJuradoExisteQueryFinder.obtener(evaluacion)).thenReturn(false);
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(evaluacion))
                .when(validator).validar(evaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
        verify(queryOutputPort, never()).consultarTodas(any());
    }
}
