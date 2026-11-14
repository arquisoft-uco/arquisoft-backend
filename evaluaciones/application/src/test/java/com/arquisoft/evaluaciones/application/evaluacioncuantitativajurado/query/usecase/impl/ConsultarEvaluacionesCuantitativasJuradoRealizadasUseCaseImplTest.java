package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.ConsultarEvaluacionesCuantitativasJuradoRealizadasValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.JuradoDeEvaluacionJuradoQueryFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
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
class ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCaseImplTest {

    @Mock
    private JuradoDeEvaluacionJuradoQueryFinder juradoDeEvaluacionJuradoQueryFinder;

    @Mock
    private ConsultarEvaluacionesCuantitativasJuradoRealizadasValidator validator;

    @Mock
    private EvaluacionCuantitativaJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCaseImpl useCase;

    @Test
    void debeRetornarEvaluaciones_cuandoJuradoEsPropietario() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionCuantitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
        var criteriaPuerto = new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado);
        var esperado = List.of(new EvaluacionCuantitativaJuradoReadModel(
                UtilUUID.generarNuevoUUID(),
                350,
                new ItemCuantitativoJuradoReadModel(
                        UtilUUID.generarNuevoUUID(), "Rigor", "desc", UtilUUID.generarNuevoUUID(), 500)));
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
    void debeNoConsultarPuerto_cuandoValidatorRechaza() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionCuantitativaJuradoRealizadaCriteria(evaluacionJurado, jurado);
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
