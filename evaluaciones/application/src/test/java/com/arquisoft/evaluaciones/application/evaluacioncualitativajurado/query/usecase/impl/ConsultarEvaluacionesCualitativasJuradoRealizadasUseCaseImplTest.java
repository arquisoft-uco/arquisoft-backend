package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport.EvaluacionCualitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.ConsultarEvaluacionesCualitativasJuradoRealizadasValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.JuradoDeEvaluacionJuradoQueryFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
class ConsultarEvaluacionesCualitativasJuradoRealizadasUseCaseImplTest {

    @Mock
    private JuradoDeEvaluacionJuradoQueryFinder juradoDeEvaluacionJuradoQueryFinder;

    @Mock
    private ConsultarEvaluacionesCualitativasJuradoRealizadasValidator validator;

    @Mock
    private EvaluacionCualitativaJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCualitativasJuradoRealizadasUseCaseImpl useCase;

    @Test
    void debeConsultar_cuandoExisteYEsPropietario() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionCualitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
        var criteriaPuerto = new EvaluacionCualitativaJuradoCriteria(evaluacionJurado);
        var esperado = List.of(new EvaluacionCualitativaJuradoReadModel(
                UtilUUID.generarNuevoUUID(),
                new ItemCualitativoJuradoReadModel(UtilUUID.generarNuevoUUID(), "Claridad", "desc"),
                new CriterioItemCualitativoJuradoReadModel(UtilUUID.generarNuevoUUID(), "Excelente", "desc")));
        when(juradoDeEvaluacionJuradoQueryFinder.obtener(evaluacionJurado)).thenReturn(jurado);
        when(queryOutputPort.consultar(criteriaPuerto)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(juradoDeEvaluacionJuradoQueryFinder, validator, queryOutputPort);
        orden.verify(juradoDeEvaluacionJuradoQueryFinder).obtener(evaluacionJurado);
        orden.verify(validator).validar(evaluacionJurado, true, jurado, jurado);
        orden.verify(queryOutputPort).consultar(criteriaPuerto);
        verify(juradoDeEvaluacionJuradoQueryFinder, times(1)).obtener(evaluacionJurado);
    }

    @Test
    void debeNoConsultar_cuandoValidatorRechaza() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionCualitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
        var otroJurado = UtilUUID.generarNuevoUUID();
        when(juradoDeEvaluacionJuradoQueryFinder.obtener(evaluacionJurado)).thenReturn(otroJurado);
        doThrow(new EvaluacionJuradoNoPerteneceJuradoException(evaluacionJurado))
                .when(validator).validar(evaluacionJurado, true, otroJurado, jurado);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
        verify(queryOutputPort, never()).consultar(any());
    }
}
