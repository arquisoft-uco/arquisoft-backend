package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesEstudianteUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesEstudianteInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesEstudianteUseCase consultarEvaluacionesEstudianteUseCase;

    @InjectMocks
    private ConsultarEvaluacionesEstudianteInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesEstudianteQuery.crear(
                estudiante, ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        var esperado = PaginatedResult.<EvaluacionReadModel>of(List.of(), 1, 5, 0);
        when(consultarEvaluacionesEstudianteUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);

        var captor = ArgumentCaptor.forClass(EvaluacionEstudianteCriteria.class);
        verify(consultarEvaluacionesEstudianteUseCase).ejecutar(captor.capture());
        var criteria = captor.getValue();
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz().toString()).contains(estudiante.toString());
    }
}
