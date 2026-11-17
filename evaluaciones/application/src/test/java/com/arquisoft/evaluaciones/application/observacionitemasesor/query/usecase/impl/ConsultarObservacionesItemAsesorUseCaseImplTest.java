package com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.AccesoEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.secondaryport.ObservacionItemAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.ConsultarObservacionesItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
class ConsultarObservacionesItemAsesorUseCaseImplTest {

    @Mock
    private AccesoEvaluacionCuantitativaAsesorQueryFinder accesoEvaluacionCuantitativaAsesorQueryFinder;

    @Mock
    private ConsultarObservacionesItemAsesorValidator validator;

    @Mock
    private ObservacionItemAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesItemAsesorUseCaseImpl useCase;

    private static ObservacionItemAsesorCriteria criteria() {
        return ObservacionItemAsesorCriteria.builder()
                .evaluacionCuantitativaAsesor(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build();
    }

    @Test
    void debeRetornarLaPagina_cuandoLaValidacionPasa() {
        // Arrange
        var criteria = criteria();
        var evaluacion = criteria.getEvaluacionCuantitativaAsesor();
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), criteria.getAsesor());
        var esperado = PaginatedResult.of(List.of(
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Sustenta el puntaje"),
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Mejorar la redaccion"),
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Citar fuentes")), 0, 10, 3);
        when(accesoEvaluacionCuantitativaAsesorQueryFinder.obtener(evaluacion)).thenReturn(acceso);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(accesoEvaluacionCuantitativaAsesorQueryFinder, validator, queryOutputPort);
        orden.verify(accesoEvaluacionCuantitativaAsesorQueryFinder).obtener(evaluacion);
        orden.verify(validator).validar(evaluacion, acceso, criteria.getAsesor());
        orden.verify(queryOutputPort).consultarTodas(criteria);
        verify(accesoEvaluacionCuantitativaAsesorQueryFinder, times(1)).obtener(any());
    }

    @Test
    void debeLanzarYNoConsultar_cuandoElValidatorRechaza() {
        // Arrange
        var criteria = criteria();
        var evaluacion = criteria.getEvaluacionCuantitativaAsesor();
        when(accesoEvaluacionCuantitativaAsesorQueryFinder.obtener(evaluacion))
                .thenReturn(AccesoEvaluacionCuantitativaAsesorDomain.VACIO);
        doThrow(new EvaluacionCuantitativaAsesorNoEncontradaException(evaluacion))
                .when(validator).validar(evaluacion, AccesoEvaluacionCuantitativaAsesorDomain.VACIO,
                        criteria.getAsesor());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(EvaluacionCuantitativaAsesorNoEncontradaException.class);
        verify(queryOutputPort, never()).consultarTodas(any());
    }
}
