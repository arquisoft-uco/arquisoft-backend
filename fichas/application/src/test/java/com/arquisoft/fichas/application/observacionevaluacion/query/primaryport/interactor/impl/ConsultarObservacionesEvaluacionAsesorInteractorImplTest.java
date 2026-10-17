package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionAsesorUseCase;
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
class ConsultarObservacionesEvaluacionAsesorInteractorImplTest {

    @Mock
    private ConsultarObservacionesEvaluacionAsesorUseCase consultarObservacionesEvaluacionAsesorUseCase;

    @Captor
    private ArgumentCaptor<ObservacionEvaluacionAsesorCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarObservacionesEvaluacionAsesorInteractorImpl interactor;

    @Test
    void debeDelegarCriteriaMapeado_cuandoEjecuta() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionAsesorQuery.crear(evaluacionFichaPerfil, asesorFicha);
        var esperado = List.of(new ObservacionEvaluacionReadModel(
                UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil, "Falta delimitar el alcance"));
        when(consultarObservacionesEvaluacionAsesorUseCase.ejecutar(
                any(ObservacionEvaluacionAsesorCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesEvaluacionAsesorUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(criteriaCaptor.getValue().asesorFicha()).isEqualTo(asesorFicha);
    }
}
