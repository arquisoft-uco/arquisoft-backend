package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionCoordinadorUseCase;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesEvaluacionCoordinadorInteractorImplTest {

    @Mock
    private ConsultarObservacionesEvaluacionCoordinadorUseCase consultarObservacionesEvaluacionCoordinadorUseCase;

    @Captor
    private ArgumentCaptor<ObservacionEvaluacionCoordinadorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarObservacionesEvaluacionCoordinadorInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConCriteria_cuandoRecibeQuery() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionCoordinadorQuery.crear(fichaPerfil);
        var esperado = List.of(new ObservacionEvaluacionReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Ajustar objetivos"));
        when(consultarObservacionesEvaluacionCoordinadorUseCase.ejecutar(
                any(ObservacionEvaluacionCoordinadorCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesEvaluacionCoordinadorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
