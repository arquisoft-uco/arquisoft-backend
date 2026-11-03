package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesDelJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesDelJuradoUseCase;
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
class ConsultarEvaluacionesDelJuradoInteractorImplTest {

    @Mock
    private ConsultarEvaluacionesDelJuradoUseCase consultarEvaluacionesDelJuradoUseCase;

    @InjectMocks
    private ConsultarEvaluacionesDelJuradoInteractorImpl interactor;

    @Test
    void debeConvertirLaQueryEnCriteria_yDelegarEnElUseCase() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesDelJuradoQuery.crear(
                jurado, ConsultaCriteriaQuery.crear(1, 5, List.of(), null));
        PaginatedResult<EvaluacionReadModel> esperado = PaginatedResult.of(List.of(), 1, 5, 0);
        when(consultarEvaluacionesDelJuradoUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);

        var captor = ArgumentCaptor.forClass(EvaluacionDelJuradoCriteria.class);
        verify(consultarEvaluacionesDelJuradoUseCase).ejecutar(captor.capture());
        var criteria = captor.getValue();
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz().toString()).contains(jurado.toString());
    }
}
