package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.ConsultarEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
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
class ConsultarEvaluacionesCuantitativasJuradoUseCaseImplTest {

    @Mock
    private EvaluacionJuradoExisteQueryFinder evaluacionJuradoExisteQueryFinder;

    @Mock
    private ConsultarEvaluacionesCuantitativasJuradoValidator validator;

    @Mock
    private EvaluacionCuantitativaJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasJuradoUseCaseImpl useCase;

    private static EvaluacionCuantitativaJuradoCriteria criteriaValido() {
        return new EvaluacionCuantitativaJuradoCriteria(UUID.randomUUID());
    }

    @Test
    void debeRetornarResultados_cuandoLaEvaluacionExiste() {
        // Arrange
        var criteria = criteriaValido();
        List<EvaluacionCuantitativaJuradoReadModel> esperado = List.of(new EvaluacionCuantitativaJuradoReadModel(
                UUID.randomUUID(), 300,
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Rigor", "desc", UUID.randomUUID(), 500)));
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(evaluacionJuradoExisteQueryFinder, validator, queryOutputPort);
        orden.verify(evaluacionJuradoExisteQueryFinder).obtener(criteria.evaluacionJuradoId());
        orden.verify(validator).validar(criteria.evaluacionJuradoId(), true);
        orden.verify(queryOutputPort).consultar(criteria);
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneEvaluacionesCuantitativas() {
        // Arrange
        var criteria = criteriaValido();
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeLanzarYNoConsultar_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var criteria = criteriaValido();
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(false);
        doThrow(new EvaluacionJuradoNoEncontradaException(criteria.evaluacionJuradoId()))
                .when(validator).validar(criteria.evaluacionJuradoId(), false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
        verify(queryOutputPort, never()).consultar(any());
    }
}
