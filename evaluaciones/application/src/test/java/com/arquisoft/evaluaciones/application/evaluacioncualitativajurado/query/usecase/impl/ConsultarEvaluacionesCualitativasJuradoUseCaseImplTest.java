package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport.EvaluacionCualitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoExisteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteQueryFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceEstudianteException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
class ConsultarEvaluacionesCualitativasJuradoUseCaseImplTest {

    @Mock
    private EvaluacionJuradoExisteQueryFinder evaluacionJuradoExisteQueryFinder;

    @Mock
    private EvaluacionJuradoPerteneceEstudianteQueryFinder evaluacionJuradoPerteneceEstudianteQueryFinder;

    @Mock
    private ConsultarEvaluacionesCualitativasJuradoValidator validator;

    @Mock
    private EvaluacionCualitativaJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasJuradoUseCaseImpl useCase;

    private static EvaluacionCualitativaJuradoCriteria criteriaValido() {
        return new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void debeRetornarResultados_cuandoExisteYPertenece() {
        // Arrange
        EvaluacionCualitativaJuradoCriteria criteria = criteriaValido();
        List<EvaluacionCualitativaJuradoReadModel> esperado = List.of(new EvaluacionCualitativaJuradoReadModel(
                UUID.randomUUID(),
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "desc"),
                new CriterioItemCualitativoJuradoReadModel(UUID.randomUUID(), "Excelente", "desc")));
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(true);
        when(evaluacionJuradoPerteneceEstudianteQueryFinder.obtener(criteria)).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        InOrder orden = inOrder(evaluacionJuradoExisteQueryFinder, evaluacionJuradoPerteneceEstudianteQueryFinder,
                validator, queryOutputPort);
        orden.verify(evaluacionJuradoExisteQueryFinder).obtener(criteria.evaluacionJuradoId());
        orden.verify(evaluacionJuradoPerteneceEstudianteQueryFinder).obtener(criteria);
        orden.verify(validator).validar(criteria.evaluacionJuradoId(), true, true);
        orden.verify(queryOutputPort).consultar(criteria);
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionNoTieneEvaluacionesCualitativas() {
        // Arrange
        EvaluacionCualitativaJuradoCriteria criteria = criteriaValido();
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(true);
        when(evaluacionJuradoPerteneceEstudianteQueryFinder.obtener(criteria)).thenReturn(true);
        when(queryOutputPort.consultar(criteria)).thenReturn(List.of());

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeLanzarYNoConsultar_cuandoLaEvaluacionNoExiste() {
        // Arrange
        EvaluacionCualitativaJuradoCriteria criteria = criteriaValido();
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(false);
        when(evaluacionJuradoPerteneceEstudianteQueryFinder.obtener(criteria)).thenReturn(false);
        doThrow(new EvaluacionJuradoNoEncontradaException(criteria.evaluacionJuradoId()))
                .when(validator).validar(criteria.evaluacionJuradoId(), false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
        verify(queryOutputPort, never()).consultar(any());
    }

    @Test
    void debeLanzarYNoConsultar_cuandoLaEvaluacionNoPerteneceAlEstudiante() {
        // Arrange
        EvaluacionCualitativaJuradoCriteria criteria = criteriaValido();
        when(evaluacionJuradoExisteQueryFinder.obtener(criteria.evaluacionJuradoId())).thenReturn(true);
        when(evaluacionJuradoPerteneceEstudianteQueryFinder.obtener(criteria)).thenReturn(false);
        doThrow(new EvaluacionJuradoNoPerteneceEstudianteException(criteria.evaluacionJuradoId()))
                .when(validator).validar(criteria.evaluacionJuradoId(), true, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionJuradoNoPerteneceEstudianteException.class);
        verify(queryOutputPort, never()).consultar(any());
    }
}
