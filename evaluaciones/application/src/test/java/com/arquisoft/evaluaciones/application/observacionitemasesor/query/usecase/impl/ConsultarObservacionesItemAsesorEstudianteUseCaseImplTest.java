package com.arquisoft.evaluaciones.application.observacionitemasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.AccesoEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.finder.EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.secondaryport.ObservacionItemAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.ConsultarObservacionesItemAsesorEstudianteValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesItemAsesorEstudianteUseCaseImplTest {

    @Mock
    private AccesoEvaluacionCuantitativaAsesorQueryFinder accesoFinder;

    @Mock
    private EstudiantesDeEvaluacionCuantitativaAsesorQueryFinder estudiantesFinder;

    @Mock
    private ConsultarObservacionesItemAsesorEstudianteValidator validator;

    @Mock
    private ObservacionItemAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesItemAsesorEstudianteUseCaseImpl useCase;

    private static ObservacionItemAsesorCriteria criteria() {
        return ObservacionItemAsesorCriteria.builder()
                .evaluacionCuantitativaAsesor(UtilUUID.generarNuevoUUID())
                .estudiante(UtilUUID.generarNuevoUUID())
                .build();
    }

    @Test
    void debeRetornarLaPagina_cuandoLaValidacionPasa() {
        // Arrange
        var criteria = criteria();
        var evaluacion = criteria.getEvaluacionCuantitativaAsesor();
        var estudiante = criteria.getEstudiante();
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var vinculados = List.of(estudiante, UtilUUID.generarNuevoUUID());
        var esperado = PaginatedResult.of(List.of(
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Sustenta el puntaje"),
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Mejorar la redaccion"),
                new ObservacionItemAsesorReadModel(UtilUUID.generarNuevoUUID(), "Citar fuentes")), 0, 10, 3);
        when(accesoFinder.obtener(evaluacion)).thenReturn(acceso);
        when(estudiantesFinder.obtener(evaluacion)).thenReturn(vinculados);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(accesoFinder, estudiantesFinder, validator, queryOutputPort);
        orden.verify(accesoFinder).obtener(evaluacion);
        orden.verify(estudiantesFinder).obtener(evaluacion);
        orden.verify(validator).validar(evaluacion, acceso, vinculados, estudiante);
        orden.verify(queryOutputPort).consultarTodas(criteria);
        verify(accesoFinder, times(1)).obtener(any());
        verify(estudiantesFinder, times(1)).obtener(any());
    }
}
