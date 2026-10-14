package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionEstudianteUseCase;
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
class ConsultarObservacionesEvaluacionEstudianteInteractorImplTest {

    @Mock
    private ConsultarObservacionesEvaluacionEstudianteUseCase consultarObservacionesEvaluacionEstudianteUseCase;

    @Captor
    private ArgumentCaptor<ObservacionEvaluacionEstudianteCriteria> criteriaCaptor;

    @InjectMocks
    private ConsultarObservacionesEvaluacionEstudianteInteractorImpl interactor;

    @Test
    void debeDelegarCriteriaMapeado_cuandoEjecuta() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionEstudianteQuery.crear(evaluacionFichaPerfil, estudiante);
        var esperado = List.of(new ObservacionEvaluacionReadModel(
                UtilUUID.generarNuevoUUID(), evaluacionFichaPerfil, "Falta delimitar el alcance"));
        when(consultarObservacionesEvaluacionEstudianteUseCase.ejecutar(
                any(ObservacionEvaluacionEstudianteCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarObservacionesEvaluacionEstudianteUseCase).ejecutar(criteriaCaptor.capture());
        assertThat(criteriaCaptor.getValue().evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(criteriaCaptor.getValue().estudiante()).isEqualTo(estudiante);
    }
}
